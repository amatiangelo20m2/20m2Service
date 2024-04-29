package com.ventimetriquadriconsulting.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EventiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventiServiceApplication.class, args);
    }
}