package com.ventimetriconsulting.service;

import com.ventimetriconsulting.entity.NotificationEntity;
import com.ventimetriconsulting.repository.Repository20m2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private final Repository20m2 notification20m2Repository;

    @Autowired
    public NotificationService(Repository20m2 notification20m2Repository) {
        this.notification20m2Repository = notification20m2Repository;
    }

    public NotificationEntity saveNotification(NotificationEntity notification) {
        log.info("Info - Storing notification {}", notification);
        return notification20m2Repository.save(notification);
    }

    public List<NotificationEntity> getAllNotifications() {
        return notification20m2Repository.findAll();
    }
}
