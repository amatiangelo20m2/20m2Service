package com.ventimetriquadriconsulting.comminucation.whatsapp.exception.customexception;

public class WhatsAppConfigurationAlreadyPresentException extends RuntimeException {
    public WhatsAppConfigurationAlreadyPresentException(String message) {
        super(message);
    }
}