package com.ventimetriquadriconsulting.restaurant.report.entity.dto;

import com.ventimetriquadriconsulting.restaurant.employee.entity.dto.EmployeeDTO;
import com.ventimetriquadriconsulting.restaurant.report.entity.EmployeePresenceReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePresenceReportDTO {

    private Long reportId;
    private String branchCode;
    private EmployeeDTO employee;
    private ZonedDateTime date;
    private boolean isPresentAtLunch;
    private boolean isPresentAtDinner;
    private boolean isHoliday;
    private boolean isIllness;
    private int workedHours;
    private String note;

    // Conversion from Entity to DTO
    public static EmployeePresenceReportDTO fromEntity(EmployeePresenceReport report) {
        return EmployeePresenceReportDTO.builder()
                .reportId(report.getReportId())
                .employee(EmployeeDTO.fromEntity(report.getEmployee())) // Convert Employee entity to DTO
                .date(report.getDate())
                .branchCode(report.getBranchCode())
                .isPresentAtLunch(report.isPresentAtLunch())
                .isPresentAtDinner(report.isPresentAtDinner())
                .isIllness(report.isIllness())
                .isHoliday(report.isHoliday())
                .workedHours(report.getWorkedHours())
                .note(report.getNote())
                .build();
    }

    // Conversion from DTO to Entity
    public EmployeePresenceReport toEntity() {
        return EmployeePresenceReport.builder()
                .reportId(this.reportId)
                .branchCode(this.branchCode)
                .employee(this.employee.toEntity()) // Convert EmployeeDTO to entity
                .date(this.date)
                .isPresentAtLunch(this.isPresentAtLunch)
                .isPresentAtDinner(this.isPresentAtDinner)
                .workedHours(this.workedHours)
                .isHoliday(this.isHoliday)
                .isIllness(this.isIllness)
                .note(this.note)
                .build();
    }

    // Conversion from Entity List to DTO List
    public static List<EmployeePresenceReportDTO> fromEntityList(List<EmployeePresenceReport> reports) {
        return reports.stream()
                .map(EmployeePresenceReportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Conversion from DTO List to Entity List
    public static List<EmployeePresenceReport> toEntityList(List<EmployeePresenceReportDTO> reportDTOs) {
        return reportDTOs.stream()
                .map(EmployeePresenceReportDTO::toEntity)
                .collect(Collectors.toList());
    }
}