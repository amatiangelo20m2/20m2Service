package com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity;

import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.Gender;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.Contract;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.JobDescription;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.RemunerationType;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.EmployeePresenceReport;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Employee {

    @Id
    @SequenceGenerator(
            name = "employee_id",
            sequenceName = "employee_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_id"
    )
    @Column(
            name = "employee_id",
            updatable = false
    )
    private Long employeeId;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "job_description")
    @Enumerated(EnumType.STRING)
    private JobDescription jobDescription;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "start_date_induction")
    private LocalDate startDateInduction;

    @Column(name = "end_date_induction")
    private LocalDate endDateInduction;

    @Column(name = "retribution")
    private double retribution;

    @Column(name = "contract_type")
    @Enumerated(EnumType.STRING)
    private Contract contractType;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "is_fired")
    private boolean isFired;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EmployeePresenceReport> reports;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "remuneration")
    @Enumerated(EnumType.STRING)
    private RemunerationType remunerationType;

}

