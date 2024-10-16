package com.ventimetriconsulting.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;


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
