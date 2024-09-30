package com.ventimetriquadriconsulting.restaurant.customer.repository;

import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneAndPrefix(String phone, String prefix);


    boolean existsByEmail(String email);
}
