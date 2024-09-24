package com.ventimetriquadriconsulting.restaurant.customer.entity.dto;

import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long customerId;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String prefix;
    private String streetAddress;
    private String city;
    private String postalCode;
    private String country;
    private String cap;
    private LocalDateTime birthDate;
    private String gender;
    private String nationality;
    private LocalDateTime registrationDate;

    /**
     * Converts a Customer entity to a CustomerDTO.
     *
     * @param customer the Customer entity
     * @return the corresponding CustomerDTO
     */
    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .prefix(customer.getPrefix())
                .streetAddress(customer.getStreetAddress())
                .city(customer.getCity())
                .postalCode(customer.getPostalCode())
                .country(customer.getCountry())
                .cap(customer.getCap())
                .birthDate(customer.getBirthDate())
                .gender(customer.getGender())
                .nationality(customer.getNationality())
                .registrationDate(customer.getRegistrationDate())
                .build();
    }

    /**
     * Converts a CustomerDTO to a Customer entity.
     *
     * @param customerDTO the CustomerDTO
     * @return the corresponding Customer entity
     */
    public static Customer toEntity(CustomerDTO customerDTO) {
        return Customer.builder()
                .customerId(customerDTO.getCustomerId())
                .name(customerDTO.getName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .prefix(customerDTO.getPrefix())
                .streetAddress(customerDTO.getStreetAddress())
                .city(customerDTO.getCity())
                .postalCode(customerDTO.getPostalCode())
                .country(customerDTO.getCountry())
                .cap(customerDTO.getCap())
                .birthDate(customerDTO.getBirthDate())
                .gender(customerDTO.getGender())
                .nationality(customerDTO.getNationality())
                .registrationDate(customerDTO.getRegistrationDate())
                .build();
    }

    /**
     * Converts a list of Customer entities to a list of CustomerDTOs.
     *
     * @param customers the list of Customer entities
     * @return the corresponding list of CustomerDTOs
     */
    public static List<CustomerDTO> fromEntityList(List<Customer> customers) {
        return customers.stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of CustomerDTOs to a list of Customer entities.
     *
     * @param customerDTOs the list of CustomerDTOs
     * @return the corresponding list of Customer entities
     */
    public static List<Customer> toEntityList(List<CustomerDTO> customerDTOs) {
        return customerDTOs.stream()
                .map(CustomerDTO::toEntity)
                .collect(Collectors.toList());
    }

}
