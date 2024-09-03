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
    private int inventarioItemsIntoCurrentStorage;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrdersCounter {
        private int ordersInLavorazione;
        private int orderDaConfermare;
        private int ordersInviato;
        private int ordersConsegnato;
        private int ordersProntoAPartire;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReservationCounter {
        private int reservationToday;
    }
}
