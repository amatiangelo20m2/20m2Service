package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.exception.customexception;

public class WhatsAppErrorConfiguration extends RuntimeException {
    public WhatsAppErrorConfiguration(String message) {
        super(message);
    }
}
