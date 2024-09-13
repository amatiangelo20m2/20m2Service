package com.ventimetriquadriconsulting.restaurant.report.entity;

import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "employee_presence_report", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePresenceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "insert_date", nullable = false)
    private ZonedDateTime insertDate;

    @Column(name = "is_present_at_lunch", nullable = false)
    private boolean isPresentAtLunch;

    @Column(name = "is_present_at_dinner", nullable = false)
    private boolean isPresentAtDinner;

    @Column(name = "worked_hours", nullable = false)
    private int workedHours;

    @Column(name = "branch_code", length = 10, nullable = false)
    private String branchCode;

}
