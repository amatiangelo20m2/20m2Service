package com.ventimetriquadriconsulting.restaurant;

import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.entity.dto.EmployeeDTO;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.employee.service.EmployeeService;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.dto.EmployeePresenceReportDTO;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.entity.dto.EmployeeReportSummaryDTO;
import com.ventimetriquadriconsulting.restaurant.manage_restaurant.report.service.ReportService;



import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.service.RestaurantService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.List;

import java.util.Optional;

@RestController
@RequestMapping("/api/restaurant")
@AllArgsConstructor
public class RestaurantController {

    private EmployeeService employeeService;
    private ReportService reportService;
    private RestaurantService restaurantService;

    @PostMapping(path = "/restaurant/retrieveconfiguration/{branchCode}")
    public ResponseEntity<RestaurantDTO> retrieveConfiguration(@PathVariable String branchCode) {
        return ResponseEntity.ok(restaurantService.retrieveRestaurantConfiguration(branchCode));
    }

    @PostMapping(path = "/employee/create/{branchCode}")
    public ResponseEntity<EmployeeDTO> createEmployee(@PathVariable String branchCode, @RequestBody EmployeeDTO employeeDto) {
        return ResponseEntity.ok(employeeService.saveEmployee(branchCode, employeeDto));
    }

    @PutMapping(path = "/employee/update/{branchCode}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String branchCode, @RequestBody EmployeeDTO employeeDto) {
        return ResponseEntity.ok(employeeService.updateEmployee(branchCode, employeeDto));
    }

    @GetMapping("/getemployee/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Optional<EmployeeDTO> employeeById = employeeService.getEmployeeById(id);
        return employeeById.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/hideemployee/{employeeId}")
    public ResponseEntity<Void> hideEmployee(@PathVariable Long employeeId) {
        employeeService.hideEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping(path = "/employee/getall")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }


    @GetMapping("/getreport/{id}")
    public ResponseEntity<EmployeePresenceReportDTO> getReportById(@PathVariable Long id) {
        Optional<EmployeePresenceReportDTO> report = reportService.getReportById(id);
        return report.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/retrievereport/{branchCode}")
    public ResponseEntity<List<EmployeePresenceReportDTO>> getReportsByBranchCodeAndDate(@PathVariable String branchCode,
                                                                                         ZonedDateTime zonedDateTime) {
        List<EmployeePresenceReportDTO> reports = reportService.retrieveReportByBranchCodeAndDate(branchCode, zonedDateTime);
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/report/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/getemployeebybranchcode/{branchCode}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeListByBranchCode(@PathVariable String branchCode) {
        List<EmployeeDTO> employeeByBranchCode = employeeService.getEmployeeByBranchCode(branchCode);
        return ResponseEntity.ok(employeeByBranchCode);
    }


    @PostMapping(path = "/create/reports")
    public ResponseEntity<List<EmployeePresenceReportDTO>> createReports(
            @RequestBody List<EmployeePresenceReportDTO> employeePresenceReportDTOS) {

        return ResponseEntity.ok(reportService.saveReports(employeePresenceReportDTOS));

    }

    @GetMapping(path = "/retrievereports/{branchCode}")
    public ResponseEntity<List<EmployeeReportSummaryDTO>> retrieveReports(@PathVariable String branchCode,
                                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime initialRangeDate,
                                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endRangeDate) {
        return ResponseEntity
                .ok(reportService.createAndRetrieveReport(
                        branchCode,
                        initialRangeDate,
                        endRangeDate));
    }
}