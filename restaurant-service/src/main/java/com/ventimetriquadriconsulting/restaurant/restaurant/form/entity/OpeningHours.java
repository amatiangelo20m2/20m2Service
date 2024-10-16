package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Entity
@Table(name = "opening_hours")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpeningHours {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ElementCollection
    @CollectionTable(name = "time_ranges_opening_hours", joinColumns = @JoinColumn(name = "opening_hours_id"))
    private List<TimeRange> timeRanges;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id",  referencedColumnName = "form_id")
    private Form form;
}