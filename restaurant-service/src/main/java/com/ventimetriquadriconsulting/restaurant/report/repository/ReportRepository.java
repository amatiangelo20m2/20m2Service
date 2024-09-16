package com.ventimetriquadriconsulting.restaurant.report.repository;

import com.ventimetriquadriconsulting.restaurant.report.entity.EmployeePresenceReport;
import com.ventimetriquadriconsulting.restaurant.report.entity.dto.EmployeeReportSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<EmployeePresenceReport, Long> {

    @Query("SELECT e FROM EmployeePresenceReport e WHERE e.branchCode = :branchCode AND CAST(e.date AS DATE) = :date")
    List<EmployeePresenceReport> findAllByDateAndBranchCode(@Param("branchCode") String branchCodem,
                                                            @Param("date") LocalDate date);

    @Query("SELECT e FROM EmployeePresenceReport e WHERE e.employee.employeeId = ?1 AND e.date = ?2")
    Optional<EmployeePresenceReport> findByEmployeeIdAndDate(Long employeeId, ZonedDateTime normalizedDate);


    @Query("SELECT new com.ventimetriquadriconsulting.restaurant.report.entity.dto.EmployeeReportSummaryDTO("
            + "emp.employeeId, "
            + "emp.firstName, "
            + "emp.lastName, "
            + "emp.gender, "
            + "emp.jobDescription, "
            + "emp.dob, "
            + "emp.hireDate, "
            + "emp.startDateInduction, "
            + "emp.qualification, "
            + "emp.partTimePercentage, "
            + "emp.level, "
            + "emp.endDate, "
            + "emp.costReference, "
            + "emp.contractType, "
            + "emp.phone, "
            + "emp.isVisible, "
            + "emp.isFired, "
            + "SUM(e.workedHours), "
            + "SUM(CASE WHEN e.isPresentAtLunch = true THEN 1 ELSE 0 END), "
            + "SUM(CASE WHEN e.isPresentAtDinner = true THEN 1 ELSE 0 END),"
            + "SUM(CASE WHEN e.isHoliday = true THEN 1 ELSE 0 END),"
            + "SUM(CASE WHEN e.isIllness = true THEN 1 ELSE 0 END),"
            + "SUM(CASE WHEN e.isRest = true THEN 1 ELSE 0 END))"
            + "FROM EmployeePresenceReport e "
            + "JOIN e.employee emp "
            + "WHERE e.branchCode = :branchCode AND e.date BETWEEN :startDate AND :endDate "
            + "GROUP BY emp.employeeId, emp.firstName, emp.lastName, emp.gender, emp.jobDescription, "
            + "emp.dob, emp.hireDate, emp.startDateInduction, emp.qualification, emp.partTimePercentage, "
            + "emp.level, emp.endDate, emp.costReference, emp.contractType, emp.phone, emp.isVisible, "
            + "emp.isFired")
    List<EmployeeReportSummaryDTO> findEmployeeReportSummary(
            @Param("branchCode") String branchCode,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate
    );
}
