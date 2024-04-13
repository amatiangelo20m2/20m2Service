package com.ventimetriconsulting.service;

import com.ventimetriconsulting.entity.FCMResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FirebaseNotificationService {

    @Value("${firebase.fcm.url}")
    private String firebaseUrl;

    private final RestTemplate firebaseRestTemplate;

    @Autowired
    public FirebaseNotificationService(RestTemplate firebaseRestTemplate) {
        this.firebaseRestTemplate = firebaseRestTemplate;
    }

    public void sendNotification(String token, String title, String body) {
        log.info("Sending message with title {}, body {}. Token in use [{}]", title, body, token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(buildNotificationRequest(token, title, body), headers);

        ResponseEntity<FCMResponse> response = firebaseRestTemplate.exchange(
                firebaseUrl,
                HttpMethod.POST,
                request,
                FCMResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Notification sent successfully. Status code: {}, Response body: {} - Class {}",
                    response.getStatusCode(),
                    response.getBody(),
                    response.getClass());
            FCMResponse body1 = response.getBody();

            log.info("Body casted: {}", body1 );

        } else {
            log.error("Failed to send notification. Status code: {}, Response body: {}", response.getStatusCode(), response.getBody());
        }
    }

    private String buildNotificationRequest(String token, String title, String body) {
        return "{"
                + "\"to\":\"" + token + "\","
                + "\"notification\":{"
                +     "\"title\":\"" + title + "\","
                +     "\"body\":\"" + body + "\""
                + "}"
                + "}";
    }
}