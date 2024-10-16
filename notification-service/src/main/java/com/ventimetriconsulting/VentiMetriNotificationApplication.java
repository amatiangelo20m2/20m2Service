package com.ventimetriconsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.ventimetriconsulting")
public class VentiMetriNotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(VentiMetriNotificationApplication.class, args);
    }
}
