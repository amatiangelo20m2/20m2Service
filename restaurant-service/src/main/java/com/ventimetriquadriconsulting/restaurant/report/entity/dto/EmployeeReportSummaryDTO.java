package com.ventimetriquadriconsulting.restaurant.report.entity.dto;

import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.Contract;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.Gender;
import com.ventimetriquadriconsulting.restaurant.employee.entity.utils_enum.JobDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeReportSummaryDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private JobDescription jobDescription;
    private LocalDate dob;
    private LocalDate hireDate;
    private LocalDate startDateInduction;
    private String qualification;
    private Double partTimePercentage;
    private String level;
    private LocalDate endDate;
    private String costReference;
    private Contract contractType;
    private String phone;
    private boolean isVisible;
    private boolean isFired;
    private Long totalWorkedHours;
    private Long lunchCount;
    private Long dinnerCount;
    private Long totalHolidays;
    private Long totalIllnessDays;
    private Long totalRestDays;
}
