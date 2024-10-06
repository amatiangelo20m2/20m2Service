package com.ventimetriquadriconsulting.comminucation.exception;

import com.ventimetriquadriconsulting.comminucation.exception.customexception.WhatsAppConfigurationAlreadyPresentException;
import com.ventimetriquadriconsulting.comminucation.exception.customexception.WhatsAppConfigurationNotFoundException;
import com.ventimetriquadriconsulting.comminucation.exception.customexception.WhatsAppErrorConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WhatsAppConfigurationNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(WhatsAppConfigurationNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(WhatsAppErrorConfiguration.class)
    public ResponseEntity<String> handleUserNotFoundException(WhatsAppErrorConfiguration exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(WhatsAppConfigurationAlreadyPresentException.class)
    public ResponseEntity<String> handleWhatsAppConfigurationAlreadyPresentException(WhatsAppConfigurationAlreadyPresentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

}
