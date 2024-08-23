package com.ventimetriconsulting.notification.service;

import com.ventimetriconsulting.notification.entity.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enqueMessage(NotificationEntity notificationEntity) {

        log.info("Sending notification message {}", notificationEntity);

        if(rabbitTemplate != null) {
            rabbitTemplate.convertAndSend("queue_20m2", notificationEntity, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        }
    }
}
