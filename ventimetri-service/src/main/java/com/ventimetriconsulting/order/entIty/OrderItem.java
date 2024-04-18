package com.ventimetriconsulting.order.entIty;

import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long orderItemId;

    private long productId;
    private String productName;
    private double quantity;
    private UnitMeasure unitMeasure;
    private double price;
    private boolean isDoneBySupplier;
    private boolean isReceived;

}
