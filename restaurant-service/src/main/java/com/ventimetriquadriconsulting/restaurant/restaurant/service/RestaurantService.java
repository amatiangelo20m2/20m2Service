package com.ventimetriquadriconsulting.restaurant.restaurant.service;

import com.ventimetriquadriconsulting.restaurant.restaurant.entity.Restaurant;
import com.ventimetriquadriconsulting.restaurant.restaurant.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.repository.RestaurantRepository;
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

    public RestaurantDTO retrieveRestaurantConfiguration(String branchCode){
        log.info("Retrieve restaurant configuration for branch code {} - Note: If not exist it will create a brand new configuration. ", branchCode);

        Optional<Restaurant> optRestaurant = restaurantRepository.findByBranchCode(branchCode);

        if(optRestaurant.isPresent()){

            log.info("Configuration branch found! {}" ,optRestaurant.get());
            return RestaurantDTO.fromEntity(optRestaurant.get());

        }else{

            log.info("Configuration not found! Creating a branch for a branch with code {}",
                    branchCode);

            Restaurant newRestaurantConfiguration = restaurantRepository.save(Restaurant.builder()
                    .restaurantId(0L)
                    .branchCode(branchCode)
                    .build());

            return RestaurantDTO.fromEntity(newRestaurantConfiguration);

        }


    }
}
