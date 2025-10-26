package com.rjtmahinay.loan.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_applications")
@Schema(description = "Loan application entity representing a customer's loan request")
public class LoanApplication {

    @Id
    @Schema(description = "Unique identifier for the loan application", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column("customer_id")
    @Schema(description = "ID of the customer applying for the loan", example = "1", required = true)
    private Long customerId;

    @Column("loan_amount")
    @Schema(description = "Requested loan amount", example = "50000.00", required = true)
    private BigDecimal loanAmount;

    @Column("loan_type")
    @Schema(description = "Type of loan being requested", example = "PERSONAL", required = true)
    private LoanType loanType;

    @Column("loan_term_months")
    @Schema(description = "Loan term in months", example = "36", required = true)
    private Integer loanTermMonths;

    @Schema(description = "Purpose of the loan", example = "Home improvement", required = true)
    private String purpose;

    @Schema(description = "Current status of the loan application", example = "SUBMITTED", accessMode = Schema.AccessMode.READ_ONLY)
    private ApplicationStatus status;

    @Column("interest_rate")
    @Schema(description = "Interest rate for the loan (set after approval)", example = "5.5", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal interestRate;

    @Column("monthly_payment")
    @Schema(description = "Calculated monthly payment amount", example = "1500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal monthlyPayment;

    @Column("approval_date")
    @Schema(description = "Date when the loan was approved", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime approvalDate;

    @Column("rejection_reason")
    @Schema(description = "Reason for loan rejection (if applicable)", example = "Insufficient credit score", accessMode = Schema.AccessMode.READ_ONLY)
    private String rejectionReason;

    @Column("credit_score")
    @Schema(description = "Customer's credit score", example = "750")
    private Integer creditScore;

    @Column("downpayment")
    @Schema(description = "Down payment amount for the loan", example = "10000.00")
    private BigDecimal downpayment;

    @Column("monthly_debt_payments")
    @Schema(description = "Customer's monthly debt payments", example = "1200.00")
    private BigDecimal monthlyDebtPayments;

    @Column("created_at")
    @Schema(description = "Timestamp when the application was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column("updated_at")
    @Schema(description = "Timestamp when the application was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(description = "Available loan types")
    public enum LoanType {
        @Schema(description = "Personal loan")
        PERSONAL,
        @Schema(description = "Auto loan")
        AUTO,
        @Schema(description = "Home/Mortgage loan")
        HOME,
        @Schema(description = "Student loan")
        STUDENT,
        @Schema(description = "Business loan")
        BUSINESS
    }

    @Schema(description = "Loan application status")
    public enum ApplicationStatus {
        @Schema(description = "Application has been submitted")
        SUBMITTED,
        @Schema(description = "Application is under review")
        UNDER_REVIEW,
        @Schema(description = "Application has been approved")
        APPROVED,
        @Schema(description = "Application has been rejected")
        REJECTED,
        @Schema(description = "Loan has been disbursed")
        DISBURSED,
        @Schema(description = "Application has been cancelled")
        CANCELLED
    }

    public LoanApplication(Long customerId, BigDecimal loanAmount, LoanType loanType,
            Integer loanTermMonths, String purpose) {
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.loanTermMonths = loanTermMonths;
        this.purpose = purpose;
        this.status = ApplicationStatus.SUBMITTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
