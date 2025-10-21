# Loan Origination System

A reactive Spring Boot application that provides comprehensive loan origination services with REST APIs for managing customers and loan applications.

## Features

- **Customer Management**: Create, update, and manage customer profiles
- **Loan Application Processing**: Submit, review, approve/reject, and disburse loans
- **Reactive Architecture**: Built with Spring WebFlux and R2DBC for high performance
- **Comprehensive Validation**: Input validation with detailed error responses
- **Database Integration**: H2 in-memory database with proper schema and indexing
- **Loan Calculation**: Automatic interest rate and monthly payment calculations

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Reactive Web Framework)  
- **Spring Data R2DBC** (Reactive Database Access)
- **H2 Database** (In-memory for development)
- **Lombok** (Code generation)
- **Maven** (Build tool)

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd loan-service

# Run the application
./mvnw spring-boot:run  # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

The application will start on `http://localhost:8080`

## API Documentation

### Customer Endpoints

#### Create Customer
```http
POST /api/v1/customers
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street, City, State",
  "dateOfBirth": "1990-01-15",
  "ssn": "123-45-6789",
  "annualIncome": 75000.00,
  "employmentStatus": "EMPLOYED"
}
```

#### Get Customer by ID
```http
GET /api/v1/customers/{id}
```

#### Get Customer by Email
```http
GET /api/v1/customers/email/{email}
```

#### Get All Customers
```http
GET /api/v1/customers
```

#### Search Customers by Name
```http
GET /api/v1/customers/search?name=John
```

#### Update Customer
```http
PUT /api/v1/customers/{id}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "+1987654321",
  "address": "456 Oak Avenue, City, State",
  "annualIncome": 85000.00
}
```

#### Delete Customer
```http
DELETE /api/v1/customers/{id}
```

### Loan Application Endpoints

#### Submit Loan Application
```http
POST /api/v1/loan-applications
Content-Type: application/json

{
  "customerId": 1,
  "loanAmount": 25000.00,
  "loanType": "PERSONAL",
  "loanTermMonths": 60,
  "purpose": "Home improvement"
}
```

**Loan Types**: `PERSONAL`, `AUTO`, `HOME`, `STUDENT`, `BUSINESS`

#### Get Loan Application by ID
```http
GET /api/v1/loan-applications/{id}
```

#### Get Applications by Customer
```http
GET /api/v1/loan-applications/customer/{customerId}
```

#### Get Applications by Status
```http
GET /api/v1/loan-applications/status/{status}
```

**Application Statuses**: `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `DISBURSED`, `CANCELLED`

#### Get Pending Applications
```http
GET /api/v1/loan-applications/pending
```

#### Get All Loan Applications
```http
GET /api/v1/loan-applications
```

#### Start Review Process
```http
PUT /api/v1/loan-applications/{id}/review
```

#### Approve Loan Application
```http
PUT /api/v1/loan-applications/{id}/approve
Content-Type: application/json

{
  "approvedAmount": 22000.00,
  "interestRate": 0.08
}
```

#### Reject Loan Application
```http
PUT /api/v1/loan-applications/{id}/reject
Content-Type: application/json

{
  "rejectionReason": "Insufficient credit score"
}
```

#### Disburse Loan
```http
PUT /api/v1/loan-applications/{id}/disburse
```

## Business Logic

### Interest Rate Calculation

The system automatically calculates interest rates based on loan type:

- **Personal Loans**: 12% base rate
- **Auto Loans**: 8% base rate  
- **Home Loans**: 6% base rate
- **Student Loans**: 5% base rate
- **Business Loans**: 10% base rate

**Rate Adjustments**:
- Loans > $50,000: 0.5% discount
- Loans < $10,000: 1% premium

### Monthly Payment Calculation

Uses standard loan amortization formula:
```
M = P * [r(1 + r)^n] / [(1 + r)^n - 1]
```
Where:
- M = Monthly payment
- P = Principal loan amount
- r = Monthly interest rate
- n = Number of payments

### Application Limits

- Minimum loan amount: $1,000
- Maximum active applications per customer: 3
- Interest rate range: 1% - 30%

## Example Workflow

1. **Create a Customer**
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith", 
    "email": "jane.smith@example.com",
    "phoneNumber": "+1555123456",
    "address": "789 Pine Street, Springfield, IL",
    "annualIncome": 65000.00,
    "employmentStatus": "EMPLOYED"
  }'
```

2. **Submit Loan Application**
```bash
curl -X POST http://localhost:8080/api/v1/loan-applications \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "loanAmount": 15000.00,
    "loanType": "AUTO", 
    "loanTermMonths": 48,
    "purpose": "Vehicle purchase"
  }'
```

3. **Review Application**
```bash
curl -X PUT http://localhost:8080/api/v1/loan-applications/1/review
```

4. **Approve Application**
```bash
curl -X PUT http://localhost:8080/api/v1/loan-applications/1/approve \
  -H "Content-Type: application/json" \
  -d '{
    "approvedAmount": 15000.00,
    "interestRate": 0.075
  }'
```

5. **Disburse Loan**
```bash
curl -X PUT http://localhost:8080/api/v1/loan-applications/1/disburse
```

## Error Handling

The API provides comprehensive error responses:

```json
{
  "timestamp": "2025-10-21T14:35:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "validationErrors": {
    "email": "Invalid email format",
    "loanAmount": "Minimum loan amount is $1,000"
  }
}
```

## Database Schema

The application uses H2 in-memory database with the following tables:

- **customers**: Customer profile information
- **loan_applications**: Loan application details and status

All tables include proper constraints, indexes, and foreign key relationships for data integrity and performance.

## Development

### Project Structure
```
src/main/java/com/rjtmahinay/loan/
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # Data access layer
├── model/             # Domain entities
└── exception/         # Error handling
```

### Running Tests
```bash
./mvnw test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
