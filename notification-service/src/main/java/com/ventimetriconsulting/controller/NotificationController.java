package com.ventimetriconsulting.controller;


import com.ventimetriconsulting.entity.dto.NotificationDTO;
import com.ventimetriconsulting.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "api/notification/")
@AllArgsConstructor
public class NotificationController {


    @Autowired
    private final NotificationService notificationService;

    /**
     *
     * @param userCode
     * @param daysInThePastToretrieveNotification - indicate how many days in the past the method will search for the notification
     * @return
     */
    @GetMapping("/user/{userCode}")
    public List<NotificationDTO> getNotificationsByUserCodeAndDateRange(
            @PathVariable String userCode, int daysInThePastToretrieveNotification) {
        return notificationService.getNotificationsByUserCodeAndDateRange(userCode, daysInThePastToretrieveNotification);
    }



}
