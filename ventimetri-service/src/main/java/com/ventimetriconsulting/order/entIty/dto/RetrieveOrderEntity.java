package com.ventimetriconsulting.order.entIty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@ToString
public class RetrieveOrderEntity {
    private String branchCode;
    private LocalDate startDate;
    private LocalDate endDate;
}
