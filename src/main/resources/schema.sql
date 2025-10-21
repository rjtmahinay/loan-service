-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    address TEXT NOT NULL,
    date_of_birth VARCHAR(20),
    ssn VARCHAR(11),
    annual_income DECIMAL(15,2),
    employment_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create loan_applications table
CREATE TABLE IF NOT EXISTS loan_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    loan_type VARCHAR(20) NOT NULL,
    loan_term_months INTEGER NOT NULL,
    purpose TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    interest_rate DECIMAL(5,4),
    monthly_payment DECIMAL(10,2),
    approval_date TIMESTAMP NULL,
    rejection_reason TEXT,
    credit_score INTEGER,
    debt_to_income_ratio DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_loan_customer 
        FOREIGN KEY (customer_id) REFERENCES customers(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_loan_amount 
        CHECK (loan_amount >= 1000.00),
    
    CONSTRAINT chk_loan_term 
        CHECK (loan_term_months > 0),
    
    CONSTRAINT chk_loan_type 
        CHECK (loan_type IN ('PERSONAL', 'AUTO', 'HOME', 'STUDENT', 'BUSINESS')),
    
    CONSTRAINT chk_application_status 
        CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'DISBURSED', 'CANCELLED'))
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_loan_applications_customer_id ON loan_applications(customer_id);
CREATE INDEX IF NOT EXISTS idx_loan_applications_status ON loan_applications(status);
CREATE INDEX IF NOT EXISTS idx_loan_applications_created_at ON loan_applications(created_at);
CREATE INDEX IF NOT EXISTS idx_loan_applications_loan_type ON loan_applications(loan_type);
