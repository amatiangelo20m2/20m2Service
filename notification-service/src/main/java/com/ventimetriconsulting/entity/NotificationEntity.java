package com.ventimetriconsulting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity(name = "Notification")
@Table(name = "notification",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"notification_id"}))
@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class NotificationEntity {

    @Id
    @SequenceGenerator(
            name = "notification_id",
            sequenceName = "notification_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notification_id"
    )
    @Column(
            name = "notification_id",
            updatable = false
    )
    private long notification_id;

    private String title;
    private String message;
    private String fmcToken;
    private String userCode;
    private String branchCode;
    private RedirectPage redirectPage;
    private ZonedDateTime timeZone;
    private boolean isSentSuccessfully;
}
