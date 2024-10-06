package com.ventimetriquadriconsulting.restaurant.customer.entity;

import com.ventimetriquadriconsulting.restaurant.restaurant.booking.entity.Booking;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {

    @Id
    @SequenceGenerator(name = "customer_id", sequenceName = "customer_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id")
    @Column(name = "customer_id", updatable = false)
    private Long customerId;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 5)
    private String prefix;

    private LocalDateTime birthDate;

    private Integer presenceCount;

    @Column(length = 100)
    private String origin;

    @Column
    private LocalDateTime lastPresence;

    @Column
    private Integer flames;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String province;

    @Column(length = 10)
    @Size(min = 4, max = 10, message = "Postal code must be between 4 and 10 characters")
    private String postalCode;

    @Column(length = 50)
    private String country;

    @Column
    private Boolean privacyConsent; // Privacy

    @Column
    private Boolean marketingConsent;

    @Column
    private Boolean profilingConsent;

    @Column
    private Boolean emailSpamOptOut;

    @Column(length = 200)
    private String tags; // Tags

    @Column(length = 500)
    private String notes; // Note

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        ZonedDateTime nowInItaly = ZonedDateTime.now(ZoneId.of("CET"));
        this.registrationDate = nowInItaly.toLocalDateTime();
        this.presenceCount = 0;
    }
}
