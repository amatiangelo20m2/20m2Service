package com.ventimetriquadriconsulting.restaurant.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "redirect_report")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedirectReport {

    @Id
    @SequenceGenerator(
            name = "redirect_report_id",
            sequenceName = "redirect_report_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "redirect_report_id"
    )
    @Column(
            name = "redirect_report_id",
            updatable = false
    )
    private long redirectReportId;
}
