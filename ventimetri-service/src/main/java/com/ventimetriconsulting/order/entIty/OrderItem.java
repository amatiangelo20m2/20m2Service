package com.ventimetriconsulting.order.entIty;

import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "OrderItem")
@Table(name = "order_item",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"order_item_id"}))
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OrderItem {

    @Id
    @SequenceGenerator(
            name = "order_item_id",
            sequenceName = "order_item_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_item_id"
    )
    @Column(
            name = "order_item_id",
            updatable = false
    )
    private long orderItemId;


    private String productId;
    private String productName;
    private double quantity;
    private UnitMeasure unitMeasure;
    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
