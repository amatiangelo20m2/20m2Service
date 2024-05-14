package com.ventimetriconsulting.notification.service;

import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.notification.entity.NotificationEntityTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    private final NotificationService notificationService;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate, NotificationService notificationService) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationService = notificationService;
    }

    public void enqueMessage(NotificationEntity notificationEntity) {

        log.info("Sending notification message {}", notificationEntity);



        for(String fcmToken : notificationEntity.getFmcToken()){
            notificationService.save(NotificationEntityTable.builder()
                    .title(notificationEntity.getTitle())
                    .insertionDate(LocalDate.now())
                    .fmcToken(fcmToken)
                    .read(false)
                    .message(notificationEntity.getMessage())
                    .redirectPage(notificationEntity.getRedirectPage())
                    .notificationId(0L)
                    .build());
        }

        if(rabbitTemplate != null) {
            rabbitTemplate.convertAndSend("queue_20m2", notificationEntity, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        }
    }
}
