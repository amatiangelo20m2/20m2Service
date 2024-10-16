package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "holiday")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Holidays {

    @Id
    @SequenceGenerator(
            name = "id",
            sequenceName = "id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "id"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(name = "dateFrom")
    private ZonedDateTime dateFrom;

    @Column(name = "dateTo")
    private ZonedDateTime dateTo;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private Form form;
}
