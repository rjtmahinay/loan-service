package com.rjtmahinay.loan.repository;

import com.rjtmahinay.loan.model.LoanApplication;
import com.rjtmahinay.loan.model.LoanApplication.ApplicationStatus;
import com.rjtmahinay.loan.model.LoanApplication.LoanType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface LoanApplicationRepository extends ReactiveCrudRepository<LoanApplication, Long> {
    
    Flux<LoanApplication> findByCustomerId(Long customerId);
    
    Flux<LoanApplication> findByStatus(ApplicationStatus status);
    
    Flux<LoanApplication> findByLoanType(LoanType loanType);
    
    @Query("SELECT * FROM loan_applications WHERE customer_id = :customerId AND status = :status")
    Flux<LoanApplication> findByCustomerIdAndStatus(Long customerId, ApplicationStatus status);
    
    @Query("SELECT * FROM loan_applications WHERE loan_amount BETWEEN :minAmount AND :maxAmount")
    Flux<LoanApplication> findByLoanAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT * FROM loan_applications WHERE created_at >= :startDate AND created_at <= :endDate")
    Flux<LoanApplication> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT * FROM loan_applications WHERE status = 'UNDER_REVIEW' ORDER BY created_at ASC")
    Flux<LoanApplication> findPendingApplicationsByCreatedDate();
    
    @Query("SELECT COUNT(*) FROM loan_applications WHERE customer_id = :customerId AND status IN ('SUBMITTED', 'UNDER_REVIEW')")
    Mono<Long> countActiveApplicationsByCustomerId(Long customerId);
    
    @Query("SELECT * FROM loan_applications WHERE approval_date >= :startDate AND status = 'APPROVED'")
    Flux<LoanApplication> findApprovedApplicationsSince(LocalDateTime startDate);
    
    @Query("SELECT COALESCE(SUM(loan_amount), 0) FROM loan_applications")
    Mono<BigDecimal> getTotalLoanValue();
    
    @Query("SELECT COALESCE(SUM(loan_amount), 0) FROM loan_applications WHERE status = :status")
    Mono<BigDecimal> getTotalLoanValueByStatus(ApplicationStatus status);
}
