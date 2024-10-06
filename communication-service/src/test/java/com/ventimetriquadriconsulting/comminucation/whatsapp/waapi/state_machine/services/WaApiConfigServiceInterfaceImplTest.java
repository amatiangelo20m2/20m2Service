package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class WaApiConfigServiceInterfaceImplTest {

    @Autowired
    WaApiConfigServiceInterfaceImpl waApiConfigService;

    @Autowired
    WhatsAppConfigurationRepository whatsAppConfigurationRepository;

    @Test
    @Transactional
    void createInstance() {

        String branchCode = "B12345533";
        WhatsAppConfigurationDTO createdConf = waApiConfigService.createAndSaveConfig(branchCode);
        assertEquals(createdConf.getBranchCode(), branchCode);
        assertEquals(WaApiConfState.NEW, createdConf.getWaApiState());

        WhatsAppConfigurationDTO confWithQR = waApiConfigService.retrieveQrCode(branchCode);

        assertEquals(createdConf.getBranchCode(), branchCode);
        assertEquals(WaApiConfState.QR, createdConf.getWaApiState());

//        Optional<Payment> payment1 = whatsAppConfigurationRepository.findById(createdConf.getId());

//        System.out.println(payment1.get());
    }


}