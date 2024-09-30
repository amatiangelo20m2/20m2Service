package com.ventimetriquadriconsulting.restaurant.exception.customexception;

public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(String message) {
        super(message);
    }
}