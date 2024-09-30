package com.ventimetriquadriconsulting.restaurant.restaurant.booking.repository;


import com.ventimetriquadriconsulting.restaurant.restaurant.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookingCode(String bookingCode);


}
