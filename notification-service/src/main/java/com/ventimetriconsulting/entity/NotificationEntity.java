package com.ventimetriconsulting.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class NotificationEntity {
    String title;
    String message;
    List<String> fmcToken;
    NotificationType notificationType;

    public enum NotificationType {
        IN_APP_NOTIFICATION, EMAIL, SMS, WHATSAPP
    }
}
