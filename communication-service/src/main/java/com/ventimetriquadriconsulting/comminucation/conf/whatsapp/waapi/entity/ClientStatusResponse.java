package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatusResponse {

    private ClientStatus clientStatus;
    private Links links;
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientStatus {

        private String status;
        private String instanceId;
        private Object data; // Assuming `data` can be null or any type
        private String instanceStatus;
        private String instanceWebhook;
        private List<Object> instanceEvents; // Assuming `instanceEvents` is a list of objects
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Links {

        private String self;
    }
}
