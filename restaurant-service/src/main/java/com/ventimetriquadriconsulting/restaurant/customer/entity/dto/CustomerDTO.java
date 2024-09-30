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
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String prefix;
    private LocalDateTime birthDate;
    private Integer presenceCount;
    private String origin;
    private LocalDateTime lastPresence;
    private Integer flames;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private Boolean privacyConsent;
    private Boolean marketingConsent;
    private Boolean profilingConsent;
    private Boolean emailSpamOptOut;
    private String tags;
    private String notes;
    private LocalDateTime registrationDate;

    /**
     * Converts a Customer entity to a CustomerDTO.
     *
     * @param customer the Customer entity
     * @return the corresponding CustomerDTO
     */
    public static CustomerDTO fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDTO.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .prefix(customer.getPrefix())
                .birthDate(customer.getBirthDate())
                .presenceCount(customer.getPresenceCount())
                .origin(customer.getOrigin())
                .lastPresence(customer.getLastPresence())
                .flames(customer.getFlames())
                .address(customer.getAddress())
                .city(customer.getCity())
                .province(customer.getProvince())
                .postalCode(customer.getPostalCode())
                .country(customer.getCountry())
                .privacyConsent(customer.getPrivacyConsent())
                .marketingConsent(customer.getMarketingConsent())
                .profilingConsent(customer.getProfilingConsent())
                .emailSpamOptOut(customer.getEmailSpamOptOut())
                .tags(customer.getTags())
                .notes(customer.getNotes())
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
        if (customerDTO == null) {
            return null;
        }

        return Customer.builder()
                .customerId(customerDTO.getCustomerId())
                .firstName(customerDTO.getFirstName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .prefix(customerDTO.getPrefix())
                .birthDate(customerDTO.getBirthDate())
                .presenceCount(customerDTO.getPresenceCount())
                .origin(customerDTO.getOrigin())
                .lastPresence(customerDTO.getLastPresence())
                .flames(customerDTO.getFlames())
                .address(customerDTO.getAddress())
                .city(customerDTO.getCity())
                .province(customerDTO.getProvince())
                .postalCode(customerDTO.getPostalCode())
                .country(customerDTO.getCountry())
                .privacyConsent(customerDTO.getPrivacyConsent())
                .marketingConsent(customerDTO.getMarketingConsent())
                .profilingConsent(customerDTO.getProfilingConsent())
                .emailSpamOptOut(customerDTO.getEmailSpamOptOut())
                .tags(customerDTO.getTags())
                .notes(customerDTO.getNotes())
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
        return customers == null ? List.of() : customers.stream()
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
        return customerDTOs == null ? List.of() : customerDTOs.stream()
                .map(CustomerDTO::toEntity)
                .collect(Collectors.toList());
    }
}
