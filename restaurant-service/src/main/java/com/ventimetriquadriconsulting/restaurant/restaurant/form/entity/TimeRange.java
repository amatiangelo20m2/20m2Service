package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeRange {

    private String timeRangeCode;
    private Integer openingHour;
    private Integer openingMinutes;
    private Integer closingHour;
    private Integer closingMinutes;
}