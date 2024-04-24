package com.ventimetriconsulting.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
@Entity(name = "Notification_Entity")
@Table(name = "notification_entity",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"notification_id"}))

public class NotificationEntityTable {
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
    private long notificationId;

    private String title;
    private String message;
    private String fmcToken;

    @Enumerated(EnumType.STRING)
    private RedirectPage redirectPage;

    private LocalDate insertionDate;

    private boolean read;

}
