package com.ventimetriconsulting.order.entIty.dto;

import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class ExcelDataArchivedOrder {
    private long productId;
    private String productName;
    private double quantity;
    private double receivedQuantity;
    private double sentQuantity;
    private UnitMeasure unitMeasure;
    private double price;
    private double vatPrice;
    private int vat;
}
