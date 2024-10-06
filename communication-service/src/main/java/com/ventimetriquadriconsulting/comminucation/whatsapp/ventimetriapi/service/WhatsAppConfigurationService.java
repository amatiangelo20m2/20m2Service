package com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.service;

import com.ventimetriquadriconsulting.comminucation.exception.customexception.WhatsAppConfigurationNotFoundException;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class WhatsAppConfigurationService {

    private WhatsAppConfigurationRepository whatsAppConfigurationRepository;

    public WhatsAppConfigurationDTO retrieveWhatsAppConfigurationDTO(String branchCode) {

        log.info("Retrieve whatsapp configuration for branch with code {}" , branchCode);

        WhatsAppConfiguration whatsAppConfiguration = whatsAppConfigurationRepository.findByBranchCode(branchCode);
                //.orElseThrow(() -> new WhatsAppConfigurationNotFoundException("Configurazione whatsApp non trovata per branch con codice [" + branchCode + "]"));

        return WhatsAppConfigurationDTO.fromEntity(whatsAppConfiguration);
    }

//    public MeResponse retrieveClientConfInfoFromWaApi(String instanceCode){
//
//        log.info("Retrive info instance with code {}", instanceCode);
//        MeResponse meResponse = waApiService.retrieveClientInfo(instanceCode);
//        log.info("Retrived info {}", meResponse);
//        return meResponse;
//
//    }
//
//    public ListInstanceResponse retrieveInstancesList() {
//        log.info("Retrieve instances list from waapi service..");
//        return waApiService.restrieveInstances();
//    }
}
