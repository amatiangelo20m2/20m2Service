package com.ventimetriquadriconsulting.restaurant.restaurant.entity.dto;

import com.ventimetriquadriconsulting.restaurant.report.entity.EmployeePresenceReport;
import com.ventimetriquadriconsulting.restaurant.report.entity.dto.EmployeePresenceReportDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {

    private long restaurantId;
    private String branchCode;

    public static RestaurantDTO fromEntity(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .restaurantId(restaurant.getRestaurantId())
                .branchCode(restaurant.getBranchCode())
                .build();
    }

    public static Restaurant toEntity(RestaurantDTO restaurantDTO) {
        return Restaurant.builder()
                .restaurantId(restaurantDTO.getRestaurantId())
                .branchCode(restaurantDTO.getBranchCode())
                .build();
    }


    public static List<RestaurantDTO> fromEntityList(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static List<Restaurant> toEntityList(List<RestaurantDTO> restaurantDTOS) {
        return restaurantDTOS.stream()
                .map(RestaurantDTO::toEntity)
                .collect(Collectors.toList());
    }


}
