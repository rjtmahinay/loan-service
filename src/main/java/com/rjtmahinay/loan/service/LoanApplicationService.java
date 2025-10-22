package com.rjtmahinay.loan.service;

import com.rjtmahinay.loan.model.LoanApplication;
import com.rjtmahinay.loan.model.LoanApplication.ApplicationStatus;
import com.rjtmahinay.loan.model.LoanApplication.LoanType;
import com.rjtmahinay.loan.repository.CustomerRepository;
import com.rjtmahinay.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    
    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    
    public Mono<LoanApplication> submitLoanApplication(LoanApplication application) {
        log.info("Submitting loan application for customer ID: {}", application.getCustomerId());
        
        // Validate customer exists
        return customerRepository.findById(application.getCustomerId())
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + application.getCustomerId())))
                .flatMap(customer -> {
                    // Check for active applications
                    return loanApplicationRepository.countActiveApplicationsByCustomerId(application.getCustomerId())
                            .flatMap(activeCount -> {
                                if (activeCount >= 3) {
                                    return Mono.error(new RuntimeException("Customer has reached maximum number of active applications"));
                                }
                                
                                // Set initial values
                                application.setStatus(ApplicationStatus.SUBMITTED);
                                application.setCreatedAt(LocalDateTime.now());
                                application.setUpdatedAt(LocalDateTime.now());
                                
                                // Calculate interest rate and monthly payment
                                calculateLoanTerms(application);
                                
                                return loanApplicationRepository.save(application);
                            });
                })
                .doOnSuccess(savedApp -> log.info("Loan application submitted with ID: {}", savedApp.getId()))
                .doOnError(error -> log.error("Error submitting loan application: {}", error.getMessage()));
    }
    
    public Mono<LoanApplication> getLoanApplicationById(Long id) {
        log.info("Fetching loan application with ID: {}", id);
        return loanApplicationRepository.findById(id)
                .doOnSuccess(app -> {
                    if (app != null) {
                        log.info("Found loan application for customer ID: {}", app.getCustomerId());
                    } else {
                        log.warn("Loan application not found with ID: {}", id);
                    }
                });
    }
    
    public Flux<LoanApplication> getLoanApplicationsByCustomerId(Long customerId) {
        log.info("Fetching loan applications for customer ID: {}", customerId);
        return loanApplicationRepository.findByCustomerId(customerId)
                .doOnNext(app -> log.debug("Found application: {}", app.getId()));
    }
    
    public Flux<LoanApplication> getLoanApplicationsByStatus(ApplicationStatus status) {
        log.info("Fetching loan applications with status: {}", status);
        return loanApplicationRepository.findByStatus(status)
                .doOnNext(app -> log.debug("Found application: {} for customer: {}", app.getId(), app.getCustomerId()));
    }
    
    public Flux<LoanApplication> getPendingApplications() {
        log.info("Fetching pending loan applications");
        return loanApplicationRepository.findPendingApplicationsByCreatedDate()
                .doOnNext(app -> log.debug("Pending application: {} submitted on: {}", app.getId(), app.getCreatedAt()));
    }
    
    public Mono<LoanApplication> reviewLoanApplication(Long id) {
        log.info("Starting review for loan application ID: {}", id);
        
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Loan application not found with ID: " + id)))
                .flatMap(application -> {
                    if (application.getStatus() != ApplicationStatus.SUBMITTED) {
                        return Mono.error(new RuntimeException("Application is not in SUBMITTED status"));
                    }
                    
                    application.setStatus(ApplicationStatus.UNDER_REVIEW);
                    application.setUpdatedAt(LocalDateTime.now());
                    return loanApplicationRepository.save(application);
                })
                .doOnSuccess(app -> log.info("Loan application {} moved to UNDER_REVIEW", app.getId()));
    }
    
    public Mono<LoanApplication> approveLoanApplication(Long id, BigDecimal approvedAmount, BigDecimal interestRate) {
        log.info("Approving loan application ID: {} with amount: {}", id, approvedAmount);
        
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Loan application not found with ID: " + id)))
                .flatMap(application -> {
                    if (application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
                        return Mono.error(new RuntimeException("Application is not under review"));
                    }
                    
                    application.setStatus(ApplicationStatus.APPROVED);
                    application.setLoanAmount(approvedAmount);
                    application.setInterestRate(interestRate);
                    application.setApprovalDate(LocalDateTime.now());
                    application.setUpdatedAt(LocalDateTime.now());
                    
                    // Recalculate monthly payment with approved terms
                    calculateMonthlyPayment(application);
                    
                    return loanApplicationRepository.save(application);
                })
                .doOnSuccess(app -> log.info("Loan application {} approved", app.getId()));
    }
    
    public Mono<LoanApplication> rejectLoanApplication(Long id, String rejectionReason) {
        log.info("Rejecting loan application ID: {} with reason: {}", id, rejectionReason);
        
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Loan application not found with ID: " + id)))
                .flatMap(application -> {
                    if (application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
                        return Mono.error(new RuntimeException("Application is not under review"));
                    }
                    
                    application.setStatus(ApplicationStatus.REJECTED);
                    application.setRejectionReason(rejectionReason);
                    application.setUpdatedAt(LocalDateTime.now());
                    
                    return loanApplicationRepository.save(application);
                })
                .doOnSuccess(app -> log.info("Loan application {} rejected", app.getId()));
    }
    
    public Mono<LoanApplication> disburseLoan(Long id) {
        log.info("Disbursing loan for application ID: {}", id);
        
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Loan application not found with ID: " + id)))
                .flatMap(application -> {
                    if (application.getStatus() != ApplicationStatus.APPROVED) {
                        return Mono.error(new RuntimeException("Application is not approved"));
                    }
                    
                    application.setStatus(ApplicationStatus.DISBURSED);
                    application.setUpdatedAt(LocalDateTime.now());
                    
                    return loanApplicationRepository.save(application);
                })
                .doOnSuccess(app -> log.info("Loan disbursed for application {}", app.getId()));
    }
    
    public Flux<LoanApplication> getAllLoanApplications() {
        log.info("Fetching all loan applications");
        return loanApplicationRepository.findAll()
                .doOnNext(app -> log.debug("Found application: {} for customer: {}", app.getId(), app.getCustomerId()));
    }
    
    public Mono<BigDecimal> getTotalLoanValue() {
        log.info("Calculating total loan value across all applications");
        return loanApplicationRepository.getTotalLoanValue()
                .doOnSuccess(total -> log.info("Total loan value calculated: {}", total))
                .doOnError(error -> log.error("Error calculating total loan value: {}", error.getMessage()));
    }
    
    public Mono<BigDecimal> getTotalLoanValueByStatus(ApplicationStatus status) {
        log.info("Calculating total loan value for applications with status: {}", status);
        return loanApplicationRepository.getTotalLoanValueByStatus(status)
                .doOnSuccess(total -> log.info("Total loan value for status {}: {}", status, total))
                .doOnError(error -> log.error("Error calculating total loan value by status: {}", error.getMessage()));
    }
    
    private void calculateLoanTerms(LoanApplication application) {
        // Simple interest rate calculation based on loan type and amount
        BigDecimal baseRate = getBaseInterestRate(application.getLoanType());
        
        // Adjust rate based on loan amount (higher amounts get better rates)
        if (application.getLoanAmount().compareTo(new BigDecimal("50000")) > 0) {
            baseRate = baseRate.subtract(new BigDecimal("0.005")); // 0.5% discount
        } else if (application.getLoanAmount().compareTo(new BigDecimal("10000")) < 0) {
            baseRate = baseRate.add(new BigDecimal("0.01")); // 1% premium
        }
        
        application.setInterestRate(baseRate);
        calculateMonthlyPayment(application);
    }
    
    private BigDecimal getBaseInterestRate(LoanType loanType) {
        return switch (loanType) {
            case PERSONAL -> new BigDecimal("0.12"); // 12%
            case AUTO -> new BigDecimal("0.08"); // 8%
            case HOME -> new BigDecimal("0.06"); // 6%
            case STUDENT -> new BigDecimal("0.05"); // 5%
            case BUSINESS -> new BigDecimal("0.10"); // 10%
        };
    }
    
    private void calculateMonthlyPayment(LoanApplication application) {
        // Calculate monthly payment using standard loan formula
        // M = P * [r(1 + r)^n] / [(1 + r)^n - 1]
        
        BigDecimal principal = application.getLoanAmount();
        BigDecimal annualRate = application.getInterestRate();
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("12"), 8, RoundingMode.HALF_UP);
        int numberOfPayments = application.getLoanTermMonths();
        
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            // No interest loan
            application.setMonthlyPayment(principal.divide(new BigDecimal(numberOfPayments), 2, RoundingMode.HALF_UP));
        } else {
            // Standard loan calculation
            BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
            BigDecimal onePlusRToN = onePlusR.pow(numberOfPayments);
            BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRToN);
            BigDecimal denominator = onePlusRToN.subtract(BigDecimal.ONE);
            
            BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
            application.setMonthlyPayment(monthlyPayment);
        }
    }
}
