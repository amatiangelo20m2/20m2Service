package com.ventimetriquadriconsulting.restaurant.exception.customexception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
