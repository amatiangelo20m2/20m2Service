package com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.dto;

import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.Employee;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.Contract;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.Gender;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.JobDescription;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.utils_enum.RemunerationType;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO {

    private Long employeeId;
    private String lastName;
    private String firstName;
    private Gender gender;
    private JobDescription jobDescription;
    private LocalDate dob;

    private LocalDate startDateInduction;
    private LocalDate endDateInduction;
    private double retribution;
    private Contract contractType;
    private String email;
    private String phone;
    private boolean isVisible;
    private boolean isFired;
    private String branchCode; // Linking to Restaurant entity with just its branch code
    private RemunerationType remunerationType;

    // Static methods for conversion between DTO and Entity

    // Convert Employee entity to EmployeeDTO
    public static EmployeeDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .lastName(employee.getLastName())
                .firstName(employee.getFirstName())
                .gender(employee.getGender())
                .jobDescription(employee.getJobDescription())
                .dob(employee.getDob())
                .startDateInduction(employee.getStartDateInduction())
                .endDateInduction(employee.getEndDateInduction())
                .retribution(employee.getRetribution())
                .contractType(employee.getContractType())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .isVisible(employee.isVisible())
                .isFired(employee.isFired())
                .branchCode(employee.getBranchCode()) // Link to Restaurant ID
                .remunerationType(employee.getRemunerationType())
                .build();
    }

    // Convert EmployeeDTO to Employee entity
    public static Employee toEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }
        return Employee.builder()
                .employeeId(dto.getEmployeeId())
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .gender(dto.getGender())
                .jobDescription(dto.getJobDescription())
                .dob(dto.getDob())
                .startDateInduction(dto.getStartDateInduction())
                .endDateInduction(dto.getEndDateInduction())
                .retribution(dto.getRetribution())
                .contractType(dto.getContractType())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .isVisible(dto.isVisible())
                .isFired(dto.isFired())
                .remunerationType(dto.getRemunerationType())
                .build();
    }

    // Convert List of Employee entities to List of EmployeeDTOs
    public static List<EmployeeDTO> fromEntityList(List<Employee> employees) {
        return employees.stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Convert List of EmployeeDTOs to List of Employee entities
    public static List<Employee> toEntityList(List<EmployeeDTO> dtos) {
        return dtos.stream()
                .map(dto -> toEntity(dto))
                .collect(Collectors.toList());
    }
}
