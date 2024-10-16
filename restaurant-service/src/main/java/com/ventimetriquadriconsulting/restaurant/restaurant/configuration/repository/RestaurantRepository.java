package com.ventimetriquadriconsulting.restaurant.restaurant.configuration.repository;

import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    Optional<Restaurant> findByBranchCode(String branchCode);
}
