package com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {

    private String branchCode;
    private LocalDateTime creationDate;

    public static RestaurantDTO fromEntity(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .branchCode(restaurant.getBranchCode())
                .creationDate(restaurant.getCreationDate())
                .build();
    }

    public static Restaurant toEntity(RestaurantDTO restaurantDTO) {
        return Restaurant.builder()
                .creationDate(restaurantDTO.getCreationDate())
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
