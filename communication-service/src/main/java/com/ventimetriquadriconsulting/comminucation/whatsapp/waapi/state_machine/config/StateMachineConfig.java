package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.config;

import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.whatsapp.ventimetriapi.repository.WhatsAppConfigurationRepository;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.CreateUpdateResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.QrCodeResponse;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.service.WaApiService;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfigEvent;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.entity.WaApiConfState;
import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.state_machine.services.WaApiConfigServiceInterfaceImpl;
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

                .and()
                .withExternal()
                .source(WaApiConfState.INSTANCE_NOT_CREATED)
                .target(WaApiConfState.INSTANCE_CREATED)
                .event(WaApiConfigEvent.CREATE_INSTANCE)
                .action(createNewConfiguration())

                .and()
                .withExternal()
                .source(WaApiConfState.INSTANCE_CREATED)
                .target(WaApiConfState.QR)
                .event(WaApiConfigEvent.RETRIEVE_QR_CODE)
                .action(retrieveQrCode())

                .and()
                .withExternal()
                .source(WaApiConfState.QR)
                .target(WaApiConfState.READY)
                .event(WaApiConfigEvent.RETRIEVE_INSTANCE_STATUS)
                .action(checkWaApiConfStatus())


                .and()
                .withExternal().source(WaApiConfState.QR).target(WaApiConfState.NOT_READY).event(WaApiConfigEvent.REBOOT)
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

    public Action<WaApiConfState, WaApiConfigEvent> createNewConfiguration() {
        return context -> {
            //TODO: do the logic to retrieve the instance - if exist and is in QR status go in QR, if not give back not create
//            if(new Random().nextInt(10) < 8) {
            log.info("Create new configuration..");
            String branchCode = String.valueOf(context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER));
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
                                .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER,
                                        context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER)).build());

                }
            }catch (Exception e){

                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER,
                                context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER)).build());
                whatsAppConfigurationRepository
                        .updateIntanceCodeToBranch(
                                "",
                                WaApiConfState.INSTANCE_NOT_CREATED,
                                branchCode);
            }
        };
    }

    private Action<WaApiConfState, WaApiConfigEvent> retrieveQrCode() {
        return context -> {
            log.info("retrieve qr code..");
            String branchCode
                    = String.valueOf(context
                    .getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER));

            WhatsAppConfiguration byBranchCode
                    = whatsAppConfigurationRepository.findByBranchCode(branchCode);


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

                    // Set the QR code into the database
                    byBranchCode.setQrCode(qrCodeResponse.getQrCode().getData().getQrCode());

                    // Exit the loop since we have a valid QR code
                    break;
                }

                // Check if we've exceeded the maximum retry time
                if (System.currentTimeMillis() - startTime >= maxRetryTime) {
                    // Optionally, handle the case where the maximum retry time has been reached
                    System.out.println("Maximum retry time reached without obtaining a valid QR code.");
                    break;
                }

                // Sleep for 2 seconds before the next retry
               haveSomeSleep();

            } while (qrCodeResponse == null
                    || qrCodeResponse.getQrCode() == null
                    || !Objects.equals(qrCodeResponse.getQrCode().getStatus(), "success")
                    || qrCodeResponse.getQrCode().getData().getQrCode().isEmpty());



//            if(new Random().nextInt(10) < 8) {
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.RETRIEVE_QR_CODE)
                    .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER,
                            context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER)).build());
//            }else{
//                System.out.println("Declined - SEI UN MINCHIONE");
//                context.getStateMachine().sendEvent(MessageBuilder.withPayload(WaApiConfigEvent.REBOOT)
//                        .setHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER, context.getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER)).build());
//            }
        };
    }


    private Action<WaApiConfState, WaApiConfigEvent> checkWaApiConfStatus() {
        return context -> {

            log.info("Check Api conf status..");
            String branchCode
                    = String.valueOf(context
                    .getMessageHeader(WaApiConfigServiceInterfaceImpl.WA_API_CONFIG_ID_HEADER));

            WhatsAppConfiguration byBranchCode
                    = whatsAppConfigurationRepository.findByBranchCode(branchCode);


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
