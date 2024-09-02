package com.ventimetriconsulting.branch.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CounterEntity {
    private OrdersCounter ordersCounter;
    private ReservationCounter reservationCounter;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrdersCounter {
        private int orderIncoming;
        private int orderToConfirm;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReservationCounter {
        private int reservationToday;
    }
}
