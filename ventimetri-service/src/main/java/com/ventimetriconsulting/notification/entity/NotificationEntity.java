package com.ventimetriconsulting.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class NotificationEntity {

    private String title;
    private String message;
    private List<String> fmcToken;
    private String userCode;
    private RedirectPage redirectPage;

}
