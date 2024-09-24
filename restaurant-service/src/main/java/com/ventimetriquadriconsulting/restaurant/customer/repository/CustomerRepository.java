package com.ventimetriquadriconsulting.restaurant.customer.repository;

import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {



}
