package com.ventimetriconsulting.notification.controller;


import com.ventimetriconsulting.notification.entity.dto.NotificationEntityDto;
import com.ventimetriconsulting.notification.service.NotificationService;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/notification/")
@AllArgsConstructor
public class NotificationController {

    private NotificationService notificationService;

    @GetMapping(path = "/retrieve")
    public ResponseEntity<List<NotificationEntityDto>> retrieveNotification(@RequestParam String fcmToken){
        try{
            return ResponseEntity.ok().body(notificationService.retrieveAll(fcmToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(path = "/setnotificationread")
    public void setNotificationReadByFcmToken(@RequestParam String fcmToken){
        notificationService.updateAllNotificationToRead(fcmToken);
    }
}
