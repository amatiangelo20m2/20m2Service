package com.ventimetriquadriconsulting.restaurant.customer.controller;


import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import com.ventimetriquadriconsulting.restaurant.customer.entity.dto.CustomerDTO;
import com.ventimetriquadriconsulting.restaurant.customer.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {


    private CustomerService customerService;

    @PostMapping("/save")
    public ResponseEntity<CustomerDTO> save(@RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customerService.createCustomer(customerDTO));
    }

    @GetMapping("/retrieve/{prefix}/{phoneNumber}")
    public ResponseEntity<CustomerDTO> findcustomerByPhoneAndPrefix(@PathVariable String prefix,
                                                                    @PathVariable String phoneNumber ) {

        Optional<Customer> customer = customerService.findcustomerByPhoneAndPrefix(phoneNumber, prefix);

        return customer
                .map(value -> ResponseEntity.ok(CustomerDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
