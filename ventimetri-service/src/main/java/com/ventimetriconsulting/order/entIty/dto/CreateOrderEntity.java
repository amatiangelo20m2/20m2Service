package com.ventimetriconsulting.order.entIty.dto;


import com.ventimetriconsulting.order.entIty.OrderTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@Builder
@ToString
public class CreateOrderEntity {

    private String userName;
    private String userCode;
    private String branchCode;

    private LocalDate insertedDate;
    private LocalDate incomingDate;

    private OrderTarget orderTarget;
    private String branchCodeTarget;
    private String supplierCodeTarget;

    private Map<Long, Double> orderItemAmountMap;

}
