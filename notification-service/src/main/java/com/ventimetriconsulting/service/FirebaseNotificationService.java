package com.ventimetriconsulting.service;

import com.ventimetriconsulting.entity.FCMResponse;
import com.ventimetriconsulting.entity.NotificationEntity;
import com.ventimetriconsulting.entity.RedirectPage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class FirebaseNotificationService {

    @Value("${firebase.fcm.url}")
    private String firebaseUrl;

    private final RestTemplate firebaseRestTemplate;


    private final NotificationService notificationService;


    @Autowired
    public FirebaseNotificationService(RestTemplate firebaseRestTemplate, NotificationService notificationService) {
        this.firebaseRestTemplate = firebaseRestTemplate;
        this.notificationService = notificationService;
    }

    public void sendNotification(NotificationEntity notificationEntity) {
        try{
            log.info("Sending message with title {}, message {}. Token in use [{}]. The user code is [{}] " +
                    "The notification will redirect user to {}",
                    notificationEntity.getTitle(),
                    notificationEntity.getMessage(),
                    notificationEntity.getFmcToken(),
                    notificationEntity.getUserCode(),
                    notificationEntity.getRedirectPage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(createFirebaseMessageJson(
                    notificationEntity.getFmcToken(),
                    notificationEntity.getTitle(),
                    notificationEntity.getMessage(),
                    notificationEntity.getRedirectPage()),
                    headers);

            ResponseEntity<FCMResponse> response = firebaseRestTemplate.exchange(
                    firebaseUrl,
                    HttpMethod.POST,
                    request,
                    FCMResponse.class);



            if (response.getStatusCode().is2xxSuccessful()) {

                log.info("Notification sent successfully. Status code: {}, Response body: {} - Class {}",
                        response.getStatusCode(),
                        response.getBody(),
                        response.getClass());

                FCMResponse body1 = response.getBody();

                log.info("Response body: {}", body1 );


                notificationEntity.setSentSuccessfully(true);
                LocalDateTime localNow = LocalDateTime.now();
                ZonedDateTime nowInGmt = localNow.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("CET"));

                notificationEntity.setTimeZone(nowInGmt);
                notificationService.saveNotification(notificationEntity);

            } else {

                log.error("Failed to send notification. " +
                        "Status code: {}, " +
                        "Response body: {}", response.getStatusCode(), response.getBody());

                LocalDateTime localNow = LocalDateTime.now();
                ZonedDateTime nowInGmt = localNow.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("CET"));

                notificationEntity.setTimeZone(nowInGmt);
                notificationEntity.setSentSuccessfully(false);
                notificationService.saveNotification(notificationEntity);
            }
        }catch(Exception e){
            log.error("Error managed without throwing: " + e);
        }
    }

    public static String createFirebaseMessageJson(String token,
                                                   String title,
                                                   String body,
                                                   RedirectPage redirectPage) {
        try {

            log.info("Build message with Token [{}], title [{}], body [{}], redirectpage [{}]", token, title, body, redirectPage);

            JSONObject messageJson = new JSONObject();
            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            JSONObject data = new JSONObject();

            notification.put("title", title);
            notification.put("body", body);

            data.put("page", redirectPage);

            message.put("token", token);
            message.put("notification", notification);
            message.put("data", data);

            messageJson.put("message", message);

            log.info("Builded message [{}]", messageJson);
            return messageJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating JSON", e);
        }
    }
}