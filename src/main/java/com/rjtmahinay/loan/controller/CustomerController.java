package com.rjtmahinay.loan.controller;

import com.rjtmahinay.loan.model.Customer;
import com.rjtmahinay.loan.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers in the loan service")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "Create a new customer", 
               description = "Creates a new customer in the system with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid customer data provided",
                    content = @Content)
    })
    public Mono<ResponseEntity<Customer>> createCustomer(
            @Parameter(description = "Customer information", required = true)
            @RequestBody Customer customer) {
        log.info("POST /api/v1/customers - Creating customer with email: {}", customer.getEmail());
        
        return customerService.createCustomer(customer)
                .map(savedCustomer -> ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", 
               description = "Retrieves a customer by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    public Mono<ResponseEntity<Customer>> getCustomerById(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/v1/customers/{} - Fetching customer", id);
        
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", 
               description = "Retrieves a customer by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    public Mono<ResponseEntity<Customer>> getCustomerByEmail(
            @Parameter(description = "Customer email address", required = true, 
                      example = "john.doe@example.com")
            @PathVariable String email) {
        log.info("GET /api/v1/customers/email/{} - Fetching customer by email", email);
        
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get all customers", 
               description = "Retrieves a list of all customers in the system")
    @ApiResponse(responseCode = "200", description = "List of customers",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = Customer.class)))
    public Flux<Customer> getAllCustomers() {
        log.info("GET /api/v1/customers - Fetching all customers");
        return customerService.getAllCustomers();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search customers by name", 
               description = "Searches for customers by first name or last name")
    @ApiResponse(responseCode = "200", description = "List of matching customers",
                content = @Content(mediaType = "application/json", 
                                 schema = @Schema(implementation = Customer.class)))
    public Flux<Customer> searchCustomersByName(
            @Parameter(description = "Name to search for", required = true, example = "John")
            @RequestParam String name) {
        log.info("GET /api/v1/customers/search?name={} - Searching customers by name", name);
        return customerService.searchCustomersByName(name);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", 
               description = "Updates an existing customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    public Mono<ResponseEntity<Customer>> updateCustomer(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Updated customer information", required = true)
            @RequestBody Customer customerUpdate) {
        log.info("PUT /api/v1/customers/{} - Updating customer", id);
        
        return customerService.updateCustomer(id, customerUpdate)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", 
               description = "Deletes a customer from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)
    })
    public Mono<ResponseEntity<Void>> deleteCustomer(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/v1/customers/{} - Deleting customer", id);
        
        return customerService.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.notFound().build());
    }
}
