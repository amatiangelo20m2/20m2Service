package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.config;

import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.exception.customexception.ConfWhatsAppError;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.entity.ClientStatusResponse;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.entity.CreateUpdateResponse;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.entity.MeResponse;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.service.WaApiService;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.entity.WaApiConfState;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.services.WaApiConfigServiceInterfaceImpl;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
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

                .and()
                .withExternal()
                .source(WaApiConfState.INSTANCE_CREATED)
                .target(WaApiConfState.READY)
                .event(WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS)
                .action(retrieveWaApiStatus())
//                .action(retrieveQrCode())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.INSTANCE_NOT_CREATED)
//                .target(WaApiConfState.INSTANCE_CREATED)
//                .event(WaApiConfigEvent.CREATE_INSTANCE)
//                .action(createNewConfiguration())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.INSTANCE_CREATED)
//                .target(WaApiConfState.QR)
//                .event(WaApiConfigEvent.RETRIEVE_QR)
//                .action(retrieveQrCode())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.QR)
//                .target(WaApiConfState.QR)
//                .event(WaApiConfigEvent.RETRIEVE_QR)
//                .action(retrieveQrCode())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.QR)
//                .target(WaApiConfState.READY)
//                .event(WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS)
//                .action(checkWaApiConfStatus())
//
//                .and()
//                .withExternal()
//                .source(WaApiConfState.NOT_READY)
//                .target(WaApiConfState.QR)
//                .event(WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS)
//                .action(retrieveQrCode())

//                .and()
//                .withExternal()
//                .source(WaApiConfState.QR)
//                .target(WaApiConfState.NOT_READY)
//                .event(WaApiConfigEvent.REBOOT)
        ;
    }

    public Action<WaApiConfState, WaApiConfigEvent> retrieveWaApiStatus() {
        return context -> {
            log.info("Check status of wa api..");

            String branchCode
                    = String.valueOf(context
                    .getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE));

            WhatsAppConfiguration byBranchCode = whatsAppConfigurationRepository
                    .findByBranchCode(branchCode)
                    .orElseThrow(() -> new NotFoundException("Branch conf not found with code " + branchCode));


            try{
                ClientStatusResponse clientStatusResponse =
                        waApiService.retrieveInstanceStatus(byBranchCode.getWaApiInstanceId());

                Thread.sleep(2000);
                if(Objects.equals(clientStatusResponse.getStatus(), "success" )
                        && clientStatusResponse.getStatus().equals("success")
                        && Objects.equals(clientStatusResponse.getClientStatus().getInstanceStatus(), "ready")){

                    log.info("Response retrieveInstanceStatus: " + clientStatusResponse);

                    MeResponse meResponse = waApiService.retrieveClientInfo(byBranchCode.getWaApiInstanceId());

                    byBranchCode.setQrCode("");
                    byBranchCode.setWaApiConfState(WaApiConfState.READY);
                    byBranchCode.setPhone(meResponse.getMe().getData().getFormattedNumber());
                    byBranchCode.setPhotoUrl(meResponse.getMe().getData().getProfilePicUrl());
                    byBranchCode.setDisplayName(meResponse.getMe().getData().getDisplayName());

                    context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.CREATE_INSTANCE)
                            .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                    context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());

                }
            } catch (Exception e){
                log.info("Errore: " + e);
                whatsAppConfigurationRepository
                        .updateIntanceCodeToBranch(
                                byBranchCode.getWaApiInstanceId(),
                                WaApiConfState.NOT_READY,
                                branchCode);

                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
                                context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());
            }
        };
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<WaApiConfState, WaApiConfigEvent> config) throws Exception {
        StateMachineListenerAdapter<WaApiConfState, WaApiConfigEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<WaApiConfState, WaApiConfigEvent> from, State<WaApiConfState, WaApiConfigEvent> to) {
                log.info(String.format("State changed from --> %s to --> %s", from, to));
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

                haveSomeSleep();
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

                whatsAppConfigurationRepository.deleteByBranchCode(branchCode);
                throw new ConfWhatsAppError("Non sono riuscito a creare una istanza whats app per la messaggistica. Errore: " + e);
//                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
//                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE,
//                                context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_BRANCH_CODE)).build());

//                whatsAppConfigurationRepository
//                        .updateIntanceCodeToBranch(
//                                "",
//                                WaApiConfState.NOT_READY,
//                                branchCode);
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Handle interrupted exception if needed
            Thread.currentThread().interrupt();
        }

    }
}
