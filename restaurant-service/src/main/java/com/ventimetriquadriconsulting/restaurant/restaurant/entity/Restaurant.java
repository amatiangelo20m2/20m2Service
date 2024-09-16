package com.ventimetriquadriconsulting.restaurant.restaurant.entity;

import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "restaurant_configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Restaurant {

    @Id
    @SequenceGenerator(name = "restaurant_id", sequenceName = "restaurant_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_id")
    @Column(name = "restaurant_id", updatable = false)
    private Long restaurantId;

    @Column(name = "branch_code", length = 10, unique = true)
    private String branchCode;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Employee> employees;

}
