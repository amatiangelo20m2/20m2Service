package com.ventimetriquadriconsulting.event.notification.entity;

import lombok.*;


@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    private String title;
    private String message;
    private String fmcToken;
    private String userCode;
    private String branchCode;
    private RedirectPage redirectPage;
}
