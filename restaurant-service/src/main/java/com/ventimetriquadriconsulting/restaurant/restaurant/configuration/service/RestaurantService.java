package com.ventimetriquadriconsulting.restaurant.restaurant.configuration.service;

import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.repository.RestaurantRepository;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public RestaurantDTO retrieveRestaurantConfiguration(String branchCode) {

        log.info("Retrieving restaurant configuration for branch code {}", branchCode);
        Optional<Restaurant> existingRestaurantConfig = restaurantRepository.findById(branchCode);

        if (existingRestaurantConfig.isPresent()) {
            log.info("Configuration found for branch code {}", branchCode);
            return RestaurantDTO.fromEntity(existingRestaurantConfig.get());
        } else {
            log.info("Configuration not found! Creating a new configuration for branch code {}", branchCode);

            Restaurant newRestaurantConfiguration = restaurantRepository.save(
                    Restaurant.builder()
                            .branchCode(branchCode)
                            .build());


            return RestaurantDTO.fromEntity(newRestaurantConfiguration);
        }

    }

    public Optional<Restaurant> findByBrancCode(String branchCode) {
        return restaurantRepository.findByBranchCode(branchCode);
    }
}
