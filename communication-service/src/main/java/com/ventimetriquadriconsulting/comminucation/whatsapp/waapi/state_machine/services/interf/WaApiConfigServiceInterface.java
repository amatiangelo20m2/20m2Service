package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services.interf;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import org.springframework.statemachine.StateMachine;

public interface WaApiConfigServiceInterface {
    WhatsAppConfigurationDTO createAndSaveConfig(String branchCode);
    WhatsAppConfigurationDTO retrieveQrCode(String branchCode);
    StateMachine<WaApiConfState, WaApiConfigEvent> retrieveWaApiConfStatus(String branchCode);
}
