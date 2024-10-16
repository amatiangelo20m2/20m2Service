package com.ventimetriconsulting.order.scheduler;

import com.ventimetriconsulting.order.service.OrderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class SchedulerOrderService {

    private final OrderService orderService;

    public SchedulerOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void performAction() {
        orderService.retrieveSupplierOrderByDateStillToUpdateToConsegnato();
    }
}
