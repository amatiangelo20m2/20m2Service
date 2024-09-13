package com.ventimetriquadriconsulting.restaurant.exception.customexception;

public class EmailAlreadyInUserException extends RuntimeException {
    public EmailAlreadyInUserException(String message) {
        super(message);
    }
}
