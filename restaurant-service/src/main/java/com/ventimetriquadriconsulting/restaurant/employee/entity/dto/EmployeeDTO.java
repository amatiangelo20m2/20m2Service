package com.ventimetriquadriconsulting.restaurant.employee.entity.dto;

import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.Contract;
import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.Gender;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.JobDescription;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.Remuneration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO implements Serializable {

    private Long employeeId;
    private String lastName;
    private String firstName;
    private Gender gender;
    private JobDescription jobDescription;
    private LocalDate dob;
    private LocalDate hireDate;
    private LocalDate startDateInduction;
    private String qualification;
    private Double partTimePercentage;
    private LocalDate endDate;
    private String level;
    private String costReference;
    private Contract contractType;
    private String email;
    private String phone;
    private boolean isVisible;
    private boolean isFired;
    private Remuneration remuneration;
    private double wage;
    private Long restaurantId;  // Add restaurant reference as restaurantId

    // Conversion from Entity to DTO
    public static EmployeeDTO fromEntity(Employee employee) {
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .gender(employee.getGender())
                .jobDescription(employee.getJobDescription())
                .dob(employee.getDob())
                .hireDate(employee.getHireDate())
                .startDateInduction(employee.getStartDateInduction())
                .qualification(employee.getQualification())
                .partTimePercentage(employee.getPartTimePercentage())
                .level(employee.getLevel())
                .endDate(employee.getEndDate())
                .costReference(employee.getCostReference())
                .contractType(employee.getContractType())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .isFired(employee.isFired())
                .isVisible(employee.isVisible())
                .remuneration(employee.getRemuneration())
                .wage(employee.getWage())
//                .restaurantId(employee.getRestaurant().getRestaurantId())
                .build();
    }

    // Conversion from DTO to Entity
    public Employee toEntity() {
        return Employee.builder()
                .employeeId(this.employeeId)
                .lastName(this.lastName)
                .firstName(this.firstName)
                .gender(this.gender)
                .jobDescription(this.jobDescription)
                .dob(this.dob)
                .hireDate(this.hireDate)
                .startDateInduction(this.startDateInduction)
                .qualification(this.qualification)
                .partTimePercentage(this.partTimePercentage)
                .level(this.level)
                .endDate(this.endDate)
                .costReference(this.costReference)
                .contractType(this.contractType)
                .email(this.email)
                .phone(this.phone)
                .isFired(this.isFired)
                .isVisible(this.isVisible)
                .remuneration(this.remuneration)  // Include remuneration
                .wage(this.wage)
                .build();
    }

    // Conversion from Entity List to DTO List
    public static List<EmployeeDTO> fromEntityList(List<Employee> employees) {
        return employees.stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Conversion from DTO List to Entity List
    public static List<Employee> toEntityList(List<EmployeeDTO> employeeDTOs) {
        return employeeDTOs.stream()
                .map(EmployeeDTO::toEntity)
                .collect(Collectors.toList());
    }
}