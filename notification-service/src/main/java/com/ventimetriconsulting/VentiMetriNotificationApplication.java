package com.ventimetriconsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class VentiMetriNotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(VentiMetriNotificationApplication.class, args);
    }
}
