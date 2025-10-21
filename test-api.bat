@echo off
echo Testing Loan Origination System API
echo ====================================

echo.
echo 1. Creating a customer...
curl -X POST http://localhost:9090/api/v1/customers ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phoneNumber\":\"+1234567890\",\"address\":\"123 Main Street\",\"annualIncome\":75000.0,\"employmentStatus\":\"EMPLOYED\"}"

echo.
echo.
echo 2. Getting all customers...
curl http://localhost:9090/api/v1/customers

echo.
echo.
echo 3. Submitting a loan application...
curl -X POST http://localhost:9090/api/v1/loan-applications ^
  -H "Content-Type: application/json" ^
  -d "{\"customerId\":1,\"loanAmount\":25000.00,\"loanType\":\"PERSONAL\",\"loanTermMonths\":60,\"purpose\":\"Home improvement\"}"

echo.
echo.
echo 4. Getting all loan applications...
curl http://localhost:9090/api/v1/loan-applications

echo.
echo.
echo 5. Starting review process for application 1...
curl -X PUT http://localhost:9090/api/v1/loan-applications/1/review

echo.
echo.
echo 6. Approving loan application 1...
curl -X PUT http://localhost:9090/api/v1/loan-applications/1/approve ^
  -H "Content-Type: application/json" ^
  -d "{\"approvedAmount\":22000.00,\"interestRate\":0.08}"

echo.
echo.
echo 7. Getting final application status...
curl http://localhost:9090/api/v1/loan-applications/1

echo.
echo.
echo API Testing Complete!
pause
