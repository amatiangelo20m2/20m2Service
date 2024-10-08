package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.services.interf;

import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;

public interface WaApiConfigServiceInterface {
    WhatsAppConfigurationDTO createAndSaveConfig(String branchCode);
    WhatsAppConfigurationDTO retrieveQrCode(String branchCode);
    WhatsAppConfigurationDTO retrieveWaApiConfStatus(String branchCode);
}
