package com.ventimetriconsulting.notification.service;

import com.ventimetriconsulting.notification.entity.NotificationEntityTable;
import com.ventimetriconsulting.notification.entity.dto.NotificationEntityDto;
import com.ventimetriconsulting.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private final NotificationRepository notificationRepository;



    @Transactional
    public List<NotificationEntityDto> retrieveAll(String fcmToken) {

        //retrieve all notification from the last 7 days
        log.info("Retrieve all notifications for token {}", fcmToken);

        List<NotificationEntityTable> allByFmcToken =
                notificationRepository
                        .findByFmcTokenAndInsertionDateBeforeOrderByNotificationIdDesc(fcmToken,
                                LocalDate.now().minusDays(7));

        List<NotificationEntityDto> dtoList = NotificationEntityDto.toDTOList(allByFmcToken);
        updateAllNotificationToRead(fcmToken);

        return dtoList;
    }

    @Transactional
    public void updateAllNotificationToRead(String fcmToken) {
        log.info("Update to read all notifications for token {}", fcmToken);
        notificationRepository.updateAllByFmcTokenToRead(fcmToken);
    }

    @Transactional
    public void save(NotificationEntityTable notificationEntityTable) {
        notificationRepository.save(notificationEntityTable);
    }
}
