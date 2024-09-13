package com.ventimetriquadriconsulting.restaurant.restaurant.repository;

import com.ventimetriquadriconsulting.restaurant.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByBranchCode(String branchCode);
}
