package com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.controller;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.service.WhatsAppConfigurationService;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.ListInstanceResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.MeResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wsapicontroller")
@AllArgsConstructor
public class WhatsAppConfigurationController {

    private WhatsAppConfigurationService whatsAppConfigurationService;

//    @GetMapping(path = "/retrieve/instances")
//    public ResponseEntity<ListInstanceResponse> retrieve(){
//        return ResponseEntity.ok(whatsAppConfigurationService.retrieveInstancesList());
//    }
    @GetMapping(path = "/retrieveconfiguration/{branchCode}")
    public ResponseEntity<WhatsAppConfigurationDTO> retrieveWhatsAppConfigurationDTO(@PathVariable String branchCode){
        return ResponseEntity.ok(whatsAppConfigurationService.retrieveWhatsAppConfigurationDTO(branchCode));
    }

//    @GetMapping(path = "/retrieve/waconfinfo/{instanceCode}")
//    public ResponseEntity<MeResponse> retrieveClientConfInfoFromWaApi(@PathVariable String instanceCode){
//        return ResponseEntity.ok(whatsAppConfigurationService.retrieveClientConfInfoFromWaApi(instanceCode));
//    }

//    @GetMapping(path = "/create/instance/{branchCode}")
//    public ResponseEntity<WhatsAppConfigurationDTO> createInstance(@PathVariable String branchCode){
//        return ResponseEntity.ok(whatsAppConfigurationService.createInstance(branchCode));
//    }

//    @PutMapping(path = "/configurenumber/{branchCode}/{phoneNumber}")
//    public ResponseEntity<WhatsAppConfigurationDTO> configureNumber(@PathVariable String branchCode, @PathVariable String phoneNumber){
//        return ResponseEntity.ok(whatsAppConfigurationService.retrieveWhatsAppConfigurationDTO(branchCode));
//    }

}
