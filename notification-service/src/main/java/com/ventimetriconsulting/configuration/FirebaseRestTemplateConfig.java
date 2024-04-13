package com.ventimetriconsulting.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FirebaseRestTemplateConfig {

    @Value("${firebase.server.key}")
    private String firebaseServerKey;

    @Bean
    public RestTemplate firebaseRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Interceptor to add Firebase server key to the headers
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "key=" + firebaseServerKey);
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });

        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}