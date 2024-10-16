package com.ventimetriconsulting.entity.dto;

import com.ventimetriconsulting.entity.NotificationEntity;
import com.ventimetriconsulting.entity.RedirectPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private long notificationId;
    private String title;
    private String message;
    private String fmcToken;
    private String userCode;
    private String branchCode;
    private RedirectPage redirectPage;
    private ZonedDateTime timeZone;
    private boolean isSentSuccessfully;

    // Conversion method for single entity to DTO
    public static NotificationDTO fromEntity(NotificationEntity entity) {
        return new NotificationDTO(
                entity.getNotification_id(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getFmcToken(),
                entity.getUserCode(),
                entity.getBranchCode(),
                entity.getRedirectPage(),
                entity.getTimeZone(),
                entity.isSentSuccessfully()
        );
    }

    public static List<NotificationDTO> fromEntityList(List<NotificationEntity> entities) {
        return entities.stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
