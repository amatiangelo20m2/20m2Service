package com.ventimetriquadriconsulting.comminucation.whatsapp.waapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListInstanceResponse {
    private List<Instance> instances;
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Instance {
        private long id;
        private String owner;
        private List<String> webhook_events;
    }
}