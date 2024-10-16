package com.ventimetriquadriconsulting.restaurant.customer.service;


import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import com.ventimetriquadriconsulting.restaurant.customer.entity.dto.CustomerDTO;
import com.ventimetriquadriconsulting.restaurant.customer.repository.CustomerRepository;
import com.ventimetriquadriconsulting.restaurant.exception.customexception.EmailAlreadyInUserException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO){

        log.info("Create new customer [{}]", customerDTO);
        if(customerRepository.existsByEmail(customerDTO.getEmail())){
            throw new EmailAlreadyInUserException("Email " + customerDTO.getEmail() + " già registrata.");
        }
        Customer savedCustomer = customerRepository.save(CustomerDTO.toEntity(customerDTO));

        return CustomerDTO.fromEntity(savedCustomer);
    }

    public Optional<Customer> findcustomerByPhoneAndPrefix(String phone, String prefix){
        log.info("Find customer by prefix {} and phone number {}", prefix, phone);
        return customerRepository.findByPhoneAndPrefix(phone, prefix);
    }

}
