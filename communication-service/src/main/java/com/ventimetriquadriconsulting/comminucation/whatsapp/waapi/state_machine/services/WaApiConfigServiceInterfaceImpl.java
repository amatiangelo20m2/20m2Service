package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.dto.WhatsAppConfigurationDTO;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services.interf.WaApiConfigServiceInterface;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public static final String WA_API_CONFIG_ID_HEADER = "wa_api_config_id";

    private final WhatsAppConfigurationRepository whatsAppConfigurationRepository;

    private final StateMachineFactory<WaApiConfState, WaApiConfigEvent> stateMachineFactory;


    @Override
    @Transactional
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

        log.info("Send event to branch code {}, Event State machine {}, stateMachine actual {}",
                branchCode,
                WaApiConfigEvent.CREATE_INSTANCE,
                stateMachine
                );

        sendEvent(branchCode, stateMachine, WaApiConfigEvent.CREATE_INSTANCE);

        WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                .findByBranchCode(branchCode);
                //.orElseThrow(() -> new NotFoundException("Branch conf not found with code: " + branchCode + ". Cannot associate the any wa api service instance" ));
        return WhatsAppConfigurationDTO.fromEntity(byBranchCode);
    }


    @Override
    @Transactional
    public WhatsAppConfigurationDTO retrieveQrCode(String branchCode) {
        StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine = build(branchCode);

        log.info("Send event to branch code {}, Event State machine {}, stateMachine actual {}",
                branchCode,
                WaApiConfigEvent.RETRIEVE_QR_CODE,
                stateMachine
        );
        //stateMachine = build(branchCode);
        sendEvent(branchCode, stateMachine, WaApiConfigEvent.RETRIEVE_QR_CODE);
        WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                .findByBranchCode(branchCode);
        return WhatsAppConfigurationDTO.fromEntity(byBranchCode);
    }

    @Override
    @Transactional
    public StateMachine<WaApiConfState, WaApiConfigEvent> retrieveWaApiConfStatus(String branchCode) {

        StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine = build(branchCode);
        sendEvent(branchCode, stateMachine, WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS);

        return stateMachine;
    }

    private void sendEvent(String branchCode,
                           StateMachine<WaApiConfState, WaApiConfigEvent> stateMachine,
                           WaApiConfigEvent waApiConfigEvent){

        Message msg = MessageBuilder.withPayload(waApiConfigEvent)
                .setHeader(WA_API_CONFIG_ID_HEADER, branchCode)
                .build();

        stateMachine.sendEvent(msg);
    }
    private StateMachine<WaApiConfState, WaApiConfigEvent> build(String branchCode) {

        WhatsAppConfiguration whatsAppConfigurationDTO = whatsAppConfigurationRepository
                .findByBranchCode(branchCode);
                //.orElseThrow(() -> new NotFoundException("Branch conf not found with code " + branchCode));

        StateMachine<WaApiConfState, WaApiConfigEvent> sm = stateMachineFactory.getStateMachine(Long.toString(whatsAppConfigurationDTO.getId()));

        sm.stop();

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.resetStateMachine(new DefaultStateMachineContext<>(whatsAppConfigurationDTO
                    .getWaApiConfState(), null, null, null));
        });

        sm.start();

        return sm;
    }
}
