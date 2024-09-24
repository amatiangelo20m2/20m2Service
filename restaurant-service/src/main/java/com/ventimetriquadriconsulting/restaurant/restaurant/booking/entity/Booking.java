package com.ventimetriquadriconsulting.restaurant.restaurant.booking.entity;

import com.ventimetriquadriconsulting.restaurant.customer.entity.Customer;
import com.ventimetriquadriconsulting.restaurant.restaurant.booking.repository.BookingRepository;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "booking")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @SequenceGenerator(
            name = "booking_id",
            sequenceName = "booking_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_id"
    )
    @Column(
            name = "booking_id",
            updatable = false
    )
    private long bookingId;

    @Column(name = "booking_code", unique = true, updatable = false)
    private String bookingCode;

    /**
     *
     * form_code is the code that identifies the form from where the reservation is coming
     *
     */
    @Column(name = "form_code", unique = true, updatable = false)
    private String formCode;


    // Many-to-One relationship with Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Min(value = 1)
    @Column(name = "number_of_people")
    private int numberOfPeople;

    @Column(name = "number_of_children")
    private int numberOfChildren;

    @Column
    private LocalDateTime bookingDate;

    @Column
    private LocalDateTime insertBookingDate;


    @Column(name = "max_dog_kg")
    private int maxDogKg;

    @Column(name = "special_requests", length = 500)
    private String specialRequests;

    @Column(name = "booking_status", length = 500)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Transient
    private BookingRepository bookingRepository;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @PrePersist
    protected void onCreate() {
        String generatedCode;
        do {
            generatedCode = RandomStringUtils.randomAlphanumeric(10);
        } while (bookingRepository.existsByBookingCode(generatedCode));
        ZonedDateTime nowInItaly = ZonedDateTime.now(ZoneId.of("CET"));

        this.insertBookingDate = nowInItaly.toLocalDateTime();
        this.bookingCode = generatedCode;
    }

}
