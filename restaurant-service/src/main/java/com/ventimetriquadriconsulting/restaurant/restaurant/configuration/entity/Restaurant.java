package com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity;

import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.Employee;
import com.ventimetriquadriconsulting.restaurant.restaurant.booking.entity.Booking;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.opening_configuration.entity.OpeningConfiguration;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "restaurant_configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Restaurant {

    @Id
    @Column(name = "branch_code", length = 10, unique = true, nullable = false)
    private String branchCode;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Employee> employees;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OpeningConfiguration> openingConfigurations;

    @PrePersist
    protected void onCreate() {
        ZonedDateTime nowInItaly = ZonedDateTime.now(ZoneId.of("CET"));
        this.creationDate = nowInItaly.toLocalDateTime();
    }
}
