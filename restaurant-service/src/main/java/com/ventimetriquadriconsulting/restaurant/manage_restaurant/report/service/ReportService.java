package com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.service;

import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.EmployeePresenceReport;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.dto.EmployeePresenceReportDTO;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.dto.EmployeeReportSummaryDTO;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.repository.ReportRepository;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    // Save a report and return the corresponding DTO
    public List<EmployeePresenceReportDTO> saveReports(List<EmployeePresenceReportDTO> employeePresenceReportDTOS) {

        List<EmployeePresenceReportDTO> result = new ArrayList<>();

        List<EmployeePresenceReport> reports = EmployeePresenceReportDTO.toEntityList(employeePresenceReportDTOS);

        for (EmployeePresenceReport employeePresenceReport : reports) {

            log.info("Processing report for employee {} on date {}", employeePresenceReport.getEmployee().getEmployeeId(), employeePresenceReport.getDate());

            // Normalize the date to remove time components (set time to start of the day)
            ZonedDateTime normalizedDate = employeePresenceReport.getDate().toLocalDate().atStartOfDay(ZoneId.systemDefault());
            employeePresenceReport.setDate(normalizedDate);

            // Check if a report already exists for this employee on the given date
            Optional<EmployeePresenceReport> existingReportOpt = reportRepository.findByEmployeeIdAndDate(
                    employeePresenceReport.getEmployee().getEmployeeId(), normalizedDate);

            if (existingReportOpt.isPresent()) {
                // Update the existing report

                EmployeePresenceReport existingReport = existingReportOpt.get();

                existingReport.setPresentAtLunch(employeePresenceReport.isPresentAtLunch());
                existingReport.setPresentAtDinner(employeePresenceReport.isPresentAtDinner());
                existingReport.setWorkedHours(employeePresenceReport.getWorkedHours());
                existingReport.setIllness(employeePresenceReport.isIllness());
                existingReport.setHoliday(employeePresenceReport.isHoliday());
                existingReport.setNote(employeePresenceReport.getNote());

                log.info("Updating existing report {}", existingReport);
                EmployeePresenceReport updatedReport = reportRepository.save(existingReport);

                result.add(EmployeePresenceReportDTO.fromEntity(updatedReport));

            } else {

                employeePresenceReport.getBranchCode();
                // Set the insert date and save the new report
                ZonedDateTime nowInGmt = ZonedDateTime.now(ZoneId.of("CET"));
                employeePresenceReport.setInsertDate(nowInGmt);

                log.info("Creating new report {}", employeePresenceReport);
                EmployeePresenceReport savedReport = reportRepository.save(employeePresenceReport);
                result.add(EmployeePresenceReportDTO.fromEntity(savedReport));
            }
        }

        return result;
    }


    // Retrieve a report by ID and return the corresponding DTO
    public Optional<EmployeePresenceReportDTO> getReportById(Long id) {
        return reportRepository.findById(id)
                .map(EmployeePresenceReportDTO::fromEntity);
    }


    // Delete a report by ID
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public List<EmployeePresenceReportDTO> retrieveReportByBranchCodeAndDate(String branchCode, ZonedDateTime date){
        log.info("Retrieve report for branch code {} in date {}", branchCode, date.toLocalDate());
        List<EmployeePresenceReport> allByDateAndBranchCode = reportRepository
                .findAllByDateAndBranchCode(branchCode, date.toLocalDate());

        return EmployeePresenceReportDTO.fromEntityList(allByDateAndBranchCode);
    }

    public List<EmployeeReportSummaryDTO> createAndRetrieveReport(
            String branchCode,
            ZonedDateTime initialRangeDate,
            ZonedDateTime endRangeDate) {

        log.info("Retrieve report for branch with code {} in a time range of [{} - {}]", branchCode, initialRangeDate, endRangeDate);

        return reportRepository.findEmployeeReportSummary(branchCode, initialRangeDate, endRangeDate);
    }

    // Retrieve reports by branch code and return them as a list of DTOs
//    public List<EmployeePresenceReportDTO> getReportsByBranchCode(String branchCode) {
//        return reportRepository.findByBranchCode(branchCode).stream()
//                .map(EmployeePresenceReportDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
}
