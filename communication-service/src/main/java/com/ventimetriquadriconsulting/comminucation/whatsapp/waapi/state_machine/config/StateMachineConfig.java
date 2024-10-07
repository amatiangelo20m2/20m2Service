package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.config;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.CreateUpdateResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.QrCodeResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.service.WaApiService;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services.WaApiConfigServiceInterfaceImpl;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@EnableStateMachineFactory
@AllArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<WaApiConfState, WaApiConfigEvent> {

    private final WhatsAppConfigurationRepository whatsAppConfigurationRepository;

    private final WaApiService waApiService;

    @Override
    public void configure(StateMachineStateConfigurer<WaApiConfState, WaApiConfigEvent> states) throws Exception {
        states.withStates()
                .initial(WaApiConfState.NEW)
                .states(EnumSet.allOf(WaApiConfState.class))
                .end(WaApiConfState.READY)
                .end(WaApiConfState.INSTANCE_NOT_CREATED)
                .end(WaApiConfState.NOT_READY);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WaApiConfState, WaApiConfigEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(WaApiConfState.NEW)
                .target(WaApiConfState.INSTANCE_CREATED)
                .event(WaApiConfigEvent.CREATE_INSTANCE)
                .action(createNewConfiguration())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.INSTANCE_NOT_CREATED)
//                .target(WaApiConfState.INSTANCE_CREATED)
//                .event(WaApiConfigEvent.CREATE_INSTANCE)
//                .action(createNewConfiguration())

                .and()
                .withExternal()
                .source(WaApiConfState.INSTANCE_CREATED)
                .target(WaApiConfState.QR)
                .event(WaApiConfigEvent.RETRIEVE_QR)
                .action(retrieveQrCode())

                .and()
                .withExternal()
                .source(WaApiConfState.QR)
                .target(WaApiConfState.READY)
                .event(WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS)
                .action(checkWaApiConfStatus())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.QR)
//                .target(WaApiConfState.NOT_READY)
//                .event(WaApiConfigEvent.REBOOT)
        ;
    }




    @Override
    public void configure(StateMachineConfigurationConfigurer<WaApiConfState, WaApiConfigEvent> config) throws Exception {
        StateMachineListenerAdapter<WaApiConfState, WaApiConfigEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<WaApiConfState, WaApiConfigEvent> from, State<WaApiConfState, WaApiConfigEvent> to) {
                log.info(String.format("State changed from %s to %s", from, to));
            }
        };
        config.withConfiguration()
                .listener(adapter);
    }
    @Transactional
    public Action<WaApiConfState, WaApiConfigEvent> createNewConfiguration() {
        return context -> {
            //TODO: do the logic to retrieve the instance - if exist and is in QR status go in QR, if not give back not create
//            if(new Random().nextInt(10) < 8) {
            log.info("Create new configuration..");
            String branchCode = String.valueOf(context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE));
            try{
                CreateUpdateResponse instance = waApiService.createInstance();

                Thread.sleep(2000);
                if(Objects.equals(instance.getStatus(), "success" )
                        && instance.getInstance().getId() != null){

                        whatsAppConfigurationRepository
                                .updateIntanceCodeToBranch(
                                        instance.getInstance().getId(),
                                        WaApiConfState.INSTANCE_CREATED,
                                        branchCode);

                        context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.CREATE_INSTANCE)
                                .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                        context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());

                }
            } catch (Exception e){
                log.info("Errore: " + e);
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());
                whatsAppConfigurationRepository
                        .updateIntanceCodeToBranch(
                                "",
                                WaApiConfState.INSTANCE_NOT_CREATED,
                                branchCode);
            }
        };
    }

    @Transactional
    public Action<WaApiConfState, WaApiConfigEvent> retrieveQrCode() {
        return context -> {
            log.info("retrieve qr code..");
            String branchCode
                    = String.valueOf(context
                    .getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE));

            WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                    .findByBranchCode(branchCode)
                    .orElseThrow(() -> new NotFoundException("Branch conf not found with code " + branchCode));

            try{

                QrCodeResponse qrCodeResponse = null;

                long startTime = System.currentTimeMillis();
                long maxRetryTime = 60000;


                do {
                    // Retrieve the QR code from the service
                    qrCodeResponse = waApiService.retrieveQrCode(byBranchCode.getWaApiInstanceId());

                    // Check if the response is valid and successful
                    if (qrCodeResponse != null
                            && Objects.equals(qrCodeResponse.getQrCode().getStatus(), "success")
                            && !qrCodeResponse.getQrCode().getData().getQrCode().isEmpty()) {

                        log.info("Current state before sending event: " + context.getStateMachine().getState().getId());
                        log.info("Sending event with header: " + context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE));

                        context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.RETRIEVE_QR)
                                .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                        context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());
                        // Set the QR code into the database
                        byBranchCode.setQrCode(qrCodeResponse.getQrCode().getData().getQrCode());
                        byBranchCode.setWaApiConfState(WaApiConfState.QR);
                        // Exit the loop since we have a valid QR code
                        break;
                    }

                    // Check if we've exceeded the maximum retry time
                    if (System.currentTimeMillis() - startTime >= maxRetryTime) {

                        log.error("Maximum retry time reached without obtaining a valid QR code.");
                        context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
                                .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                        context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());

                        byBranchCode.setWaApiConfState(WaApiConfState.NOT_READY);
                        break;
                    }

                    // Sleep for 2 seconds before the next retry
                    haveSomeSleep();

                } while (qrCodeResponse == null
                        || qrCodeResponse.getQrCode() == null
                        || !Objects.equals(qrCodeResponse.getQrCode().getStatus(), "success")
                        || qrCodeResponse.getQrCode().getData().getQrCode().isEmpty());

            }catch(Exception e){
                log.info("SEI UN MINCHIONE: " + e);
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());

                byBranchCode.setWaApiConfState(WaApiConfState.NOT_READY);
            }

        };
    }


    private Action<WaApiConfState, WaApiConfigEvent> checkWaApiConfStatus() {
        return context -> {

            log.info("Check Api conf status..");
            String branchCode
                    = String.valueOf(context
                    .getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE));

            WhatsAppConfiguration byBranchCode
                    = whatsAppConfigurationRepository.findByBranchCode(branchCode).orElseThrow(() -> new NotFoundException("Branch conf not found with code " + branchCode));;


        };
    }

    private void haveSomeSleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Handle interrupted exception if needed
            Thread.currentThread().interrupt();
        }

    }
}
