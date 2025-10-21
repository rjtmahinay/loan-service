package com.rjtmahinay.loan.service;

import com.rjtmahinay.loan.model.Customer;
import com.rjtmahinay.loan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public Mono<Customer> createCustomer(Customer customer) {
        log.info("Creating new customer with email: {}", customer.getEmail());
        
        // Check if customer with email already exists
        return customerRepository.existsByEmail(customer.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Customer with email " + customer.getEmail() + " already exists"));
                    }
                    customer.setCreatedAt(LocalDateTime.now());
                    customer.setUpdatedAt(LocalDateTime.now());
                    return customerRepository.save(customer);
                })
                .doOnSuccess(savedCustomer -> log.info("Customer created with ID: {}", savedCustomer.getId()))
                .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
    }
    
    public Mono<Customer> getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id)
                .doOnSuccess(customer -> {
                    if (customer != null) {
                        log.info("Found customer: {}", customer.getEmail());
                    } else {
                        log.warn("Customer not found with ID: {}", id);
                    }
                });
    }
    
    public Mono<Customer> getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email)
                .doOnSuccess(customer -> {
                    if (customer != null) {
                        log.info("Found customer with ID: {}", customer.getId());
                    } else {
                        log.warn("Customer not found with email: {}", email);
                    }
                });
    }
    
    public Flux<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll()
                .doOnNext(customer -> log.debug("Found customer: {}", customer.getEmail()));
    }
    
    public Flux<Customer> searchCustomersByName(String name) {
        log.info("Searching customers by name: {}", name);
        return customerRepository.findByNameContaining(name)
                .doOnNext(customer -> log.debug("Found customer: {} {}", customer.getFirstName(), customer.getLastName()));
    }
    
    public Mono<Customer> updateCustomer(Long id, Customer customerUpdate) {
        log.info("Updating customer with ID: {}", id);
        
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + id)))
                .flatMap(existingCustomer -> {
                    // Update fields
                    if (customerUpdate.getFirstName() != null) {
                        existingCustomer.setFirstName(customerUpdate.getFirstName());
                    }
                    if (customerUpdate.getLastName() != null) {
                        existingCustomer.setLastName(customerUpdate.getLastName());
                    }
                    if (customerUpdate.getPhoneNumber() != null) {
                        existingCustomer.setPhoneNumber(customerUpdate.getPhoneNumber());
                    }
                    if (customerUpdate.getAddress() != null) {
                        existingCustomer.setAddress(customerUpdate.getAddress());
                    }
                    if (customerUpdate.getDateOfBirth() != null) {
                        existingCustomer.setDateOfBirth(customerUpdate.getDateOfBirth());
                    }
                    if (customerUpdate.getAnnualIncome() != null) {
                        existingCustomer.setAnnualIncome(customerUpdate.getAnnualIncome());
                    }
                    if (customerUpdate.getEmploymentStatus() != null) {
                        existingCustomer.setEmploymentStatus(customerUpdate.getEmploymentStatus());
                    }
                    
                    existingCustomer.setUpdatedAt(LocalDateTime.now());
                    return customerRepository.save(existingCustomer);
                })
                .doOnSuccess(updatedCustomer -> log.info("Customer updated: {}", updatedCustomer.getId()))
                .doOnError(error -> log.error("Error updating customer: {}", error.getMessage()));
    }
    
    public Mono<Void> deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + id)))
                .flatMap(customer -> customerRepository.delete(customer))
                .doOnSuccess(unused -> log.info("Customer deleted with ID: {}", id))
                .doOnError(error -> log.error("Error deleting customer: {}", error.getMessage()));
    }
}
