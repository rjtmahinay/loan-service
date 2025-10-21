package com.rjtmahinay.loan.repository;

import com.rjtmahinay.loan.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    
    Mono<Customer> findByEmail(String email);
    
    Mono<Boolean> existsByEmail(String email);
    
    @Query("SELECT * FROM customers WHERE first_name ILIKE '%' || :name || '%' OR last_name ILIKE '%' || :name || '%'")
    Flux<Customer> findByNameContaining(String name);
    
    Mono<Customer> findBySsn(String ssn);
    
    @Query("SELECT * FROM customers WHERE annual_income >= :minIncome")
    Flux<Customer> findByAnnualIncomeGreaterThanEqual(Double minIncome);
}
