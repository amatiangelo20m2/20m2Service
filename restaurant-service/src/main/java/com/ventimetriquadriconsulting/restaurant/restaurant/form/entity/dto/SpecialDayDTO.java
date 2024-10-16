package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.SpecialDay;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialDayDTO {
    private Long id;
    private ZonedDateTime specialDate;
    private List<TimeRange> timeRanges; // Assuming TimeRange is already a defined DTO
    private boolean isClosed;
    private String description;

    public static SpecialDayDTO fromEntity(SpecialDay specialDay) {
        if (specialDay == null) {
            return null;
        }

        return SpecialDayDTO.builder()
                .id(specialDay.getId())
                .specialDate(specialDay.getSpecialDate())
                .timeRanges(specialDay.getTimeRanges()) // Convert as needed
                .isClosed(specialDay.isClosed())
                .description(specialDay.getDescription())
                .build();
    }

    public SpecialDay toEntity() {
        SpecialDay specialDay = new SpecialDay();
        specialDay.setId(this.id);
        specialDay.setSpecialDate(this.specialDate);
        specialDay.setTimeRanges(this.timeRanges); // Convert as needed
        specialDay.setClosed(this.isClosed);
        specialDay.setDescription(this.description);
        return specialDay;
    }
}
