package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.Holidays;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HolidaysDTO {
    private Long id;
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private String description;

    public static HolidaysDTO fromEntity(Holidays holidays) {
        if (holidays == null) {
            return null;
        }

        return HolidaysDTO.builder()
                .id(holidays.getId())
                .dateFrom(holidays.getDateFrom())
                .dateTo(holidays.getDateTo())
                .description(holidays.getDescription())
                .build();
    }

    public Holidays toEntity() {
        Holidays holidays = new Holidays();
        holidays.setId(this.id);
        holidays.setDateFrom(this.dateFrom);
        holidays.setDateTo(this.dateTo);
        holidays.setDescription(this.description);
        return holidays;
    }
}
