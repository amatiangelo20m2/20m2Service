package com.ventimetriconsulting.notification.entity.dto;

import com.ventimetriconsulting.notification.entity.NotificationEntityTable;
import com.ventimetriconsulting.notification.entity.RedirectPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Builder
@ToString
public class NotificationEntityDto {

    private long notificationId;
    private String title;
    private String message;
    private String fmcToken;
    private String userCode;
    private RedirectPage redirectPage;
    private LocalDate insertionDate;

    public static NotificationEntityDto fromEntity(NotificationEntityTable entity) {
        return NotificationEntityDto.builder()
                .notificationId(entity.getNotificationId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .fmcToken(entity.getFmcToken())
                .redirectPage(entity.getRedirectPage())
                .insertionDate(entity.getInsertionDate())
                .build();
    }

    public static List<NotificationEntityDto> toDTOList(List<NotificationEntityTable> orders) {
        return orders.stream().map(NotificationEntityDto::fromEntity).collect(Collectors.toList());
    }
}
