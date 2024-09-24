package com.ventimetriquadriconsulting.restaurant.customer.service;


import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import com.ventimetriquadriconsulting.restaurant.customer.entity.dto.CustomerDTO;
import com.ventimetriquadriconsulting.restaurant.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDTO createCustomer(CustomerDTO customerDTO){

        log.info("Saving following customer {}", customerDTO);
        Customer savedCustomer = customerRepository.save(CustomerDTO.toEntity(customerDTO));

        return CustomerDTO.fromEntity(savedCustomer);


    }

}
