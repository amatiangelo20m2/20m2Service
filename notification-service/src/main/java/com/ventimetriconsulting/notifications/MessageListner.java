package com.ventimetriconsulting.notifications;

import com.ventimetriconsulting.entity.NotificationEntity;
import com.ventimetriconsulting.service.FirebaseNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListner {
    private final FirebaseNotificationService notificationService;
    @Autowired
    public MessageListner(FirebaseNotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @RabbitListener(queues = "queue_20m2")
    public void receive(NotificationEntity notificationEntity) {
            log.info("Send notification: {}", notificationEntity);

            notificationService.sendNotification(notificationEntity);

    }
}