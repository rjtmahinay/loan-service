package com.rjtmahinay.loan.controller;

import com.rjtmahinay.loan.model.LoanApplication;
import com.rjtmahinay.loan.model.LoanApplication.ApplicationStatus;
import com.rjtmahinay.loan.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Tag(name = "Loan Application Management", description = "APIs for managing loan applications and their lifecycle")
public class LoanApplicationController {
    
    private final LoanApplicationService loanApplicationService;
    
    @PostMapping
    @Operation(summary = "Submit loan application", 
               description = "Submits a new loan application for processing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Loan application submitted successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "400", description = "Invalid application data provided",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> submitLoanApplication(
            @Parameter(description = "Loan application details", required = true)
            @RequestBody LoanApplication application) {
        log.info("POST /api/v1/loan-applications - Submitting loan application for customer: {}", application.getCustomerId());
        
        return loanApplicationService.submitLoanApplication(application)
                .map(savedApplication -> ResponseEntity.status(HttpStatus.CREATED).body(savedApplication))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get loan application by ID", 
               description = "Retrieves a loan application by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "404", description = "Loan application not found",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> getLoanApplicationById(
            @Parameter(description = "Loan application ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/v1/loan-applications/{} - Fetching loan application", id);
        
        return loanApplicationService.getLoanApplicationById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get loan applications by customer ID", 
               description = "Retrieves all loan applications for a specific customer")
    @ApiResponse(responseCode = "200", description = "List of loan applications for the customer",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = LoanApplication.class)))
    public Flux<LoanApplication> getLoanApplicationsByCustomerId(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long customerId) {
        log.info("GET /api/v1/loan-applications/customer/{} - Fetching applications for customer", customerId);
        return loanApplicationService.getLoanApplicationsByCustomerId(customerId);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get loan applications by status", 
               description = "Retrieves all loan applications with a specific status")
    @ApiResponse(responseCode = "200", description = "List of loan applications with the specified status",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = LoanApplication.class)))
    public Flux<LoanApplication> getLoanApplicationsByStatus(
            @Parameter(description = "Application status", required = true, 
                      example = "UNDER_REVIEW", 
                      schema = @Schema(implementation = ApplicationStatus.class))
            @PathVariable ApplicationStatus status) {
        log.info("GET /api/v1/loan-applications/status/{} - Fetching applications by status", status);
        return loanApplicationService.getLoanApplicationsByStatus(status);
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending loan applications", 
               description = "Retrieves all loan applications that are pending review")
    @ApiResponse(responseCode = "200", description = "List of pending loan applications",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = LoanApplication.class)))
    public Flux<LoanApplication> getPendingApplications() {
        log.info("GET /api/v1/loan-applications/pending - Fetching pending applications");
        return loanApplicationService.getPendingApplications();
    }
    
    @GetMapping
    @Operation(summary = "Get all loan applications", 
               description = "Retrieves all loan applications in the system")
    @ApiResponse(responseCode = "200", description = "List of all loan applications",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = LoanApplication.class)))
    public Flux<LoanApplication> getAllLoanApplications() {
        log.info("GET /api/v1/loan-applications - Fetching all loan applications");
        return loanApplicationService.getAllLoanApplications();
    }
    
    @PutMapping("/{id}/review")
    @Operation(summary = "Start loan application review", 
               description = "Changes the status of a loan application to under review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application review started",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "400", description = "Cannot review application in current state",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> reviewLoanApplication(
            @Parameter(description = "Loan application ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("PUT /api/v1/loan-applications/{}/review - Starting review", id);
        
        return loanApplicationService.reviewLoanApplication(id)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve loan application", 
               description = "Approves a loan application with specified amount and interest rate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application approved successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "400", description = "Cannot approve application in current state",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> approveLoanApplication(
            @Parameter(description = "Loan application ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Approval details including amount and interest rate", required = true)
            @RequestBody ApprovalRequest approvalRequest) {
        log.info("PUT /api/v1/loan-applications/{}/approve - Approving with amount: {}", 
                id, approvalRequest.getApprovedAmount());
        
        return loanApplicationService.approveLoanApplication(
                        id, 
                        approvalRequest.getApprovedAmount(), 
                        approvalRequest.getInterestRate())
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject loan application", 
               description = "Rejects a loan application with a specified reason")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application rejected successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "400", description = "Cannot reject application in current state",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> rejectLoanApplication(
            @Parameter(description = "Loan application ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Rejection details including reason", required = true)
            @RequestBody RejectionRequest rejectionRequest) {
        log.info("PUT /api/v1/loan-applications/{}/reject - Rejecting with reason: {}", 
                id, rejectionRequest.getRejectionReason());
        
        return loanApplicationService.rejectLoanApplication(id, rejectionRequest.getRejectionReason())
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @PutMapping("/{id}/disburse")
    @Operation(summary = "Disburse loan", 
               description = "Disburses an approved loan to the customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan disbursed successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = LoanApplication.class))),
        @ApiResponse(responseCode = "400", description = "Cannot disburse loan in current state",
                    content = @Content)
    })
    public Mono<ResponseEntity<LoanApplication>> disburseLoan(
            @Parameter(description = "Loan application ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("PUT /api/v1/loan-applications/{}/disburse - Disbursing loan", id);
        
        return loanApplicationService.disburseLoan(id)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    // DTOs for request bodies
    @Data
    @Schema(description = "Request body for approving a loan application")
    public static class ApprovalRequest {
        @Schema(description = "Approved loan amount", example = "25000.00", required = true)
        private BigDecimal approvedAmount;
        
        @Schema(description = "Interest rate for the loan", example = "5.5", required = true)
        private BigDecimal interestRate;
    }
    
    @Data
    @Schema(description = "Request body for rejecting a loan application")
    public static class RejectionRequest {
        @Schema(description = "Reason for rejecting the loan application", 
                example = "Insufficient credit score", required = true)
        private String rejectionReason;
    }
}
