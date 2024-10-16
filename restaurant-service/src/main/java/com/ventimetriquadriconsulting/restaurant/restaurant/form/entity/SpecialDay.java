package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "special_day")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialDay {

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

    @Column(name = "special_date", nullable = false)
    private ZonedDateTime specialDate;

    @ElementCollection
    @CollectionTable(name = "time_ranges_special_day", joinColumns = @JoinColumn(name = "special_day_id"))
    private List<TimeRange> timeRanges = new ArrayList<>();

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id",  referencedColumnName = "form_id")
    private Form form;
}