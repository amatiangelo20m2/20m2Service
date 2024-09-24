package com.ventimetriquadriconsulting.restaurant.restaurant.booking.service;

import com.ventimetriquadriconsulting.restaurant.restaurant.booking.repository.BookingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class BookingService {


    private final BookingRepository bookingRepository;



}
