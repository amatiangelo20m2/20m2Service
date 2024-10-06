package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.service;

import com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class WaApiInterface {

    public abstract CreateUpdateResponse createInstance();
    public abstract ListInstanceResponse restrieveInstances();
    public abstract MeResponse retrieveClientInfo(String instanceCode);
    public abstract QrCodeResponse retrieveQrCode(String instanceId);
    public abstract void deleteInstance(String instanceCode);
    public abstract void rebootInstance(String instanceId);
    public abstract void sendMessage(String instanceId, String phone, String messageToSend);
    public abstract String retrievePhoto(String instanceId, String phone);
    public abstract ClientStatusResponse retrieveInstanceStatus(String instanceId);

    public void haveSomeTimeToSleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            log.warn("Sleep time between creation instance on waapi server not working. Nothing bad actually, the process can be go on");
        }
    }

}
