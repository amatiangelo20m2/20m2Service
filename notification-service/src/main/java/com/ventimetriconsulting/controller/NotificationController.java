package com.ventimetriconsulting.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping(path = "api/notification/")
@AllArgsConstructor
public class NotificationController {


    @GetMapping(path = "/checksstatus")
    public ResponseEntity<?> getGreeating(){

        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime nowInGmt = localNow.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("CET"));

        System.out.println("Current time in GMT: " + nowInGmt);

        return ResponseEntity.status(HttpStatus.OK).body("<h1>Hello, </h1>I'm Alive! - Sono le " + nowInGmt);
    }



}
