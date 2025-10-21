package com.rjtmahinay.loan.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Loan Service API",
        version = "1.0.0",
        description = "A comprehensive loan management system that handles customer management and loan application processing. " +
                     "This API provides endpoints for creating and managing customers, submitting loan applications, " +
                     "and processing loan approvals through the complete loan lifecycle.",
        contact = @Contact(
            name = "API Support",
            email = "me@rjtmahinay.com",
            url = "https://github.com/rjtmahinay"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Development Server",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Demo Server",
            url = "https://loan-service-git-rjtmahinay-dev.apps.rm1.0a51.p1.openshiftapps.com"
        )
    }
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Loan Service API")
                        .version("1.0.0")
                        .description("A comprehensive loan management system that handles customer management and loan application processing. " +
                                   "This API provides endpoints for creating and managing customers, submitting loan applications, " +
                                   "and processing loan approvals through the complete loan lifecycle.\n\n" +
                                   "## Features\n" +
                                   "- **Customer Management**: Create, read, update, and delete customer records\n" +
                                   "- **Loan Applications**: Submit and track loan applications\n" +
                                   "- **Application Processing**: Review, approve, reject, and disburse loans\n" +
                                   "- **Status Tracking**: Monitor application status throughout the lifecycle\n\n" +
                                   "## Loan Application Lifecycle\n" +
                                   "1. **SUBMITTED** - Initial application submission\n" +
                                   "2. **UNDER_REVIEW** - Application is being reviewed\n" +
                                   "3. **APPROVED** - Application approved with terms\n" +
                                   "4. **REJECTED** - Application rejected with reason\n" +
                                   "5. **DISBURSED** - Approved loan has been disbursed\n" +
                                   "6. **CANCELLED** - Application cancelled by customer\n\n" +
                                   "## Supported Loan Types\n" +
                                   "- **PERSONAL** - Personal loans for various purposes\n" +
                                   "- **AUTO** - Vehicle financing\n" +
                                   "- **HOME** - Mortgage and home equity loans\n" +
                                   "- **STUDENT** - Educational financing\n" +
                                   "- **BUSINESS** - Commercial and business loans"));
    }
}
