package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.controller;

import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.services.WaApiConfigServiceInterfaceImpl;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wsapicontroller")
@AllArgsConstructor
public class WhatsAppConfigurationController {

    private WaApiConfigServiceInterfaceImpl waApiConfigService;
    @GetMapping(path = "/retrieve/waconfstatus/{branchCode}")
    public ResponseEntity<WhatsAppConfigurationDTO> retrieveWaApiConfStatus(@PathVariable String branchCode){
        WhatsAppConfigurationDTO whatsAppConfigurationDTO = waApiConfigService.retrieveWaApiConfStatus(branchCode);
        return ResponseEntity.status(HttpStatus.OK).body(whatsAppConfigurationDTO);
    }

    @GetMapping(path = "/createconf/{branchCode}")
    public ResponseEntity<WhatsAppConfigurationDTO> createConfWaApi(@PathVariable String branchCode){

        WhatsAppConfigurationDTO whatsAppConfigurationDTO = waApiConfigService.createAndSaveConfig(branchCode);

        return ResponseEntity.status(HttpStatus.OK).body(whatsAppConfigurationDTO);
    }

    @GetMapping(path = "/retrieveqr/{branchCode}")
    public ResponseEntity<WhatsAppConfigurationDTO> retrieveQr(@PathVariable String branchCode){

        WhatsAppConfigurationDTO waConfWithQR = waApiConfigService
                .retrieveQrCode(branchCode);
        return ResponseEntity.status(HttpStatus.OK).body(waConfWithQR);
    }

    @DeleteMapping(path = "/deleteConf/{branchCode}")
    public ResponseEntity<WhatsAppConfigurationDTO> deleteConfWaApi(@PathVariable String branchCode){
        waApiConfigService.deleteConf(branchCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(path = "/sendmessage/{instanceId}")
    public ResponseEntity<Void> sendMessage(@PathVariable String instanceId,
                                            @RequestParam String message,
                                            @RequestParam String number){

        waApiConfigService.sendMessage(instanceId, number, message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    @ExceptionHandler(WhatsAppConfigurationNotFoundException.class)
//    public ResponseEntity<Void> handleWhatsAppConfigurationNotFoundException(WhatsAppConfigurationNotFoundException ex) {
//        // Return 204 No Content
//        return ResponseEntity.noContent().build();
//    }
}
