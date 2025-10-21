package com.rjtmahinay.loan.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("customers")
@Schema(description = "Customer entity representing a loan applicant")
public class Customer {
    
    @Id
    @Schema(description = "Unique identifier for the customer", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Column("first_name")
    @Schema(description = "Customer's first name", example = "John", required = true)
    private String firstName;
    
    @Column("last_name")
    @Schema(description = "Customer's last name", example = "Doe", required = true)
    private String lastName;
    
    @Schema(description = "Customer's email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @Column("phone_number")
    @Schema(description = "Customer's phone number", example = "+1-555-123-4567", required = true)
    private String phoneNumber;
    
    @Schema(description = "Customer's home address", example = "123 Main St, Anytown, ST 12345", required = true)
    private String address;
    
    @Column("date_of_birth")
    @Schema(description = "Customer's date of birth", example = "1990-01-15")
    private String dateOfBirth;
    
    @Schema(description = "Customer's Social Security Number", example = "123-45-6789")
    private String ssn;
    
    @Column("annual_income")
    @Schema(description = "Customer's annual income in USD", example = "75000.0")
    private Double annualIncome;
    
    @Column("employment_status")
    @Schema(description = "Customer's employment status", example = "EMPLOYED", 
            allowableValues = {"EMPLOYED", "UNEMPLOYED", "SELF_EMPLOYED", "RETIRED", "STUDENT"})
    private String employmentStatus;
    
    @Column("created_at")
    @Schema(description = "Timestamp when the customer record was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    @Schema(description = "Timestamp when the customer record was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    public Customer(String firstName, String lastName, String email, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
