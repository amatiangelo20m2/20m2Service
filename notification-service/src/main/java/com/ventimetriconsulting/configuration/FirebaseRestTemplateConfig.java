package com.ventimetriconsulting.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class FirebaseRestTemplateConfig {

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    private String bearerToken;

    @Bean
    public RestTemplate firebaseRestTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        bearerToken = getAccessToken();

        System.out.println("Token retrieved: " + bearerToken);
        // Interceptor to add Firebase server key to the headers
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + bearerToken);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
    //this token is refreshed every 45 minutes
    @Scheduled(fixedRate = 2700000)
    public void refreshAccessToken() {
        this.bearerToken = getAccessToken();
        System.out.println("Token refreshed: " + bearerToken);
    }
    public static String getAccessToken() {
        try {
            InputStream serviceAccountStream = FirebaseRestTemplateConfig
                    .class.getClassLoader()
                    .getResourceAsStream("service-account.json");

            if (serviceAccountStream == null) {
                throw new RuntimeException("service-account.json file not found in resources");
            }

            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(serviceAccountStream)
                    .createScoped(SCOPES);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}