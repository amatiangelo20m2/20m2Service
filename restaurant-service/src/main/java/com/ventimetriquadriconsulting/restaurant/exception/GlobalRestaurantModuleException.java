package com.ventimetriquadriconsulting.restaurant.exception;

import com.ventimetriquadriconsulting.restaurant.exception.customexception.EmailAlreadyInUserException;
import com.ventimetriquadriconsulting.restaurant.exception.customexception.ReservationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalRestaurantModuleException {

    @ExceptionHandler(EmailAlreadyInUserException.class)
    public ResponseEntity<String> handleEmailAlreadyInUserException(EmailAlreadyInUserException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<String> handleReservationNotFoundException(ReservationNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(exception.getMessage());
    }

}
