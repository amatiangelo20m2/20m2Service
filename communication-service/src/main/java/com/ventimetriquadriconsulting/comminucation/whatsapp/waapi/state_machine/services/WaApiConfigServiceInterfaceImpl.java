package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services;

import com.ventimetriquadriconsulting.comminucation.whatsapp.exception.customexception.WhatsAppConfigurationNotFoundException;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.QrCodeResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.service.WaApiService;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services.interf.WaApiConfigServiceInterface;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class WaApiConfigServiceInterfaceImpl implements WaApiConfigServiceInterface {

    public static final String WA_API_BRANCH_CODE = "wa_api_branch_code";

    private final WhatsAppConfigurationRepository whatsAppConfigurationRepository;

    private final StateMachineFactory<WaApiConfState, WaApiConfigEvent> stateMachineFactory;

    private final WaApiService waApiService;

    @Override
    public WhatsAppConfigurationDTO createAndSaveConfig(String branchCode) {
        log.info("Create new configuration waapi for branch with code {}", branchCode);

        WhatsAppConfiguration createWaConfEntity = WhatsAppConfiguration
                .builder()
                .waApiInstanceId("")
                .phone("")
                .waApiConfState(WaApiConfState.NEW)
                .qrCode("")
                .branchCode(branchCode)
                .creationDate(null)
                .id(0L)
                .lastError("")
                .build();

        whatsAppConfigurationRepository.save(createWaConfEntity);

        StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine = build(branchCode);

        log.info("Send event to branch code {}, Event State machine {}, stateMachine actual state {}",
                branchCode,
                WaApiConfigEvent.CREATE_INSTANCE,
                stateMachine.getState().getId()
                );

        sendEvent(branchCode, stateMachine, WaApiConfigEvent.CREATE_INSTANCE);

        WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                .findByBranchCode(branchCode)
                .orElseThrow(() -> new WhatsAppConfigurationNotFoundException("Branch conf not found with code " + branchCode));

        return WhatsAppConfigurationDTO.fromEntity(byBranchCode);
    }


    @Override
    @Transactional
    public WhatsAppConfigurationDTO retrieveQrCode(String branchCode) {

        WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                .findByBranchCode(branchCode).orElseThrow(() -> new WhatsAppConfigurationNotFoundException("Branch conf not found with code " + branchCode));

        log.info("Retrieve QR code for instance{} - Branch code{}",
                byBranchCode.getWaApiInstanceId(),
                branchCode
        );

        QrCodeResponse qrCodeResponse = waApiService.retrieveQrCode(byBranchCode.getWaApiInstanceId());
        byBranchCode.setQrCode(qrCodeResponse.getQrCode().getData().getQrCode());

        return WhatsAppConfigurationDTO.fromEntity(byBranchCode);
    }

    @Override
    @Transactional
    public WhatsAppConfigurationDTO retrieveWaApiConfStatus(String branchCode) {

        log.info("Retrieve wa api status for branch with code {}", branchCode);


        Optional<WhatsAppConfiguration> whatsAppConfiguration = whatsAppConfigurationRepository
                .findByBranchCode(branchCode);

        if(whatsAppConfiguration.isPresent()){
            StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine = build(whatsAppConfiguration.get().getBranchCode());

            sendEvent(branchCode,
                    stateMachine,
                    WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS);

            whatsAppConfiguration = whatsAppConfigurationRepository.findByBranchCode(branchCode);

            return WhatsAppConfigurationDTO.fromEntity(whatsAppConfiguration.get());
        }else{
            throw new WhatsAppConfigurationNotFoundException("Branch conf not found with code " + branchCode);
        }

    }


    private void sendEvent(String branchCode,
                           StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine,
                           WaApiConfigEvent waApiConfigEvent){

        Message msg = MessageBuilder.withPayload(waApiConfigEvent)
                .setHeader(WA_API_BRANCH_CODE, branchCode)
                .build();

        stateMachine.sendEvent(msg);
    }
    private StateMachine<WaApiConfState, WaApiConfigEvent> build(String branchCode) {

        WhatsAppConfiguration whatsAppConfigurationDTO = whatsAppConfigurationRepository
                .findByBranchCode(branchCode)
                .orElseThrow(() -> new WhatsAppConfigurationNotFoundException("Branch conf not found with code " + branchCode));

        StateMachine<WaApiConfState, WaApiConfigEvent> sm = stateMachineFactory.getStateMachine(
                Long.toString(whatsAppConfigurationDTO.getId()));

        sm.stop();

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.resetStateMachine(new DefaultStateMachineContext<>(whatsAppConfigurationDTO
                    .getWaApiConfState(), null, null, null));
        });

        sm.start();

        return sm;
    }

    @Transactional
    @Modifying
    public void deleteConf(String branchCode) {
        WhatsAppConfiguration whatsAppConfiguration = whatsAppConfigurationRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new WhatsAppConfigurationNotFoundException("Branch conf not found with code " + branchCode));

        String waApiInstanceId = whatsAppConfiguration.getWaApiInstanceId();
        if(!waApiInstanceId.isEmpty()){
            log.info("Deleting whatsapp waapi instance with id {}", waApiInstanceId);
            waApiService.deleteInstance(waApiInstanceId);
            log.info("Deleting whatsapp conf from db for branch with code {}", branchCode);
            whatsAppConfigurationRepository.deleteById(whatsAppConfiguration.getId());
        }
    }
}
