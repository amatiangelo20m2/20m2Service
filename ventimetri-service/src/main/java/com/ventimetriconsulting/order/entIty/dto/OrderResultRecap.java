package com.ventimetriconsulting.order.entIty.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class OrderResultRecap {

    List<DetailedProductRecap> incomingsOrders;
    List<DetailedProductRecap> outgoingOrders;


    @AllArgsConstructor
    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    public static class DetailedProductRecap {
        String code;
        String name;
        List<ExcelDataArchivedOrder> excelDataArchivedOrderList;
    }
}
