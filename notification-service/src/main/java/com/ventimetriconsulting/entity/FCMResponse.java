package com.ventimetriconsulting.entity;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FCMResponse {
    private long multicastId;
    private int success;
    private int failure;
    private int canonicalIds;
    private List<FCMResult> results;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class FCMResult {
            private String error;
    }
}
