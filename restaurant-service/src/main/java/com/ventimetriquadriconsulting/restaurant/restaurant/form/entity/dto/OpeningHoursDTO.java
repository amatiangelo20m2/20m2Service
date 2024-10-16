package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.OpeningHours;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpeningHoursDTO {
    private Long id;
    private DayOfWeek dayOfWeek;
    private List<TimeRange> timeRanges; // Assuming TimeRange is already a defined DTO
    private boolean isClosed;

    public static OpeningHoursDTO fromEntity(OpeningHours openingHours) {
        if (openingHours == null) {
            return null;
        }

        return OpeningHoursDTO.builder()
                .id(openingHours.getId())
                .dayOfWeek(openingHours.getDayOfWeek())
                .timeRanges(openingHours.getTimeRanges()) // Convert as needed
                .isClosed(openingHours.isClosed())
                .build();
    }

    public OpeningHours toEntity() {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setId(this.id);
        openingHours.setDayOfWeek(this.dayOfWeek);
        openingHours.setTimeRanges(this.timeRanges); // Convert as needed
        openingHours.setClosed(this.isClosed);
        return openingHours;
    }
}
