package com.ventimetriconsulting.service;

import com.ventimetriconsulting.entity.NotificationEntity;
import com.ventimetriconsulting.entity.dto.NotificationDTO;
import com.ventimetriconsulting.repository.Repository20m2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private final Repository20m2 notification20m2Repository;

    @Autowired
    public NotificationService(Repository20m2 notification20m2Repository) {
        this.notification20m2Repository = notification20m2Repository;
    }

    public void saveNotification(NotificationEntity notification) {
        log.info("Info - Storing notification {}", notification);
        notification20m2Repository.save(notification);
    }

    public List<NotificationDTO> getNotificationsByUserCodeAndDateRange(
            String userCode, int daysInThePast) {


        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime nowInGmt = localNow.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("CET"));


        log.info("Retrieve notification for user with code {} between date {} - {}", userCode, nowInGmt.minusDays(daysInThePast), nowInGmt);
        List<NotificationEntity> byUserCodeAndTimeZoneBetween = notification20m2Repository.findByUserCodeAndTimeZoneBetween(userCode, nowInGmt.minusDays(daysInThePast), nowInGmt);

        return NotificationDTO.fromEntityList(byUserCodeAndTimeZoneBetween);


    }
}
