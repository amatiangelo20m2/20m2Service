package com.ventimetriconsulting.order.entIty.dto;

import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

//    private long orderItemId;
    private long productId;
    private String productName;
    private double quantity;
    private double receivedQuantity;
    private double sentQuantity;
    private UnitMeasure unitMeasure;
    private double price;
    private boolean isDoneBySupplier;
    private boolean isReceived;


    public static OrderItemDto fromEntity(OrderItem orderItem) {
        return OrderItemDto.builder()
//                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .receivedQuantity(orderItem.getReceivedQuantity())
                .sentQuantity(orderItem.getSentQuantity())
                .productName(orderItem.getProductName())
                .unitMeasure(orderItem.getUnitMeasure())
                .price(orderItem.getPrice())
                .isDoneBySupplier(orderItem.isDoneBySupplier())
                .isReceived(orderItem.isReceived())
                .build();
    }

    public static OrderItem toEntity(OrderItemDto orderItemDto) {
        return OrderItem.builder()
//                .orderItemId(orderItemDto.getOrderItemId())
                .productId(orderItemDto.getProductId())
                .productName(orderItemDto.getProductName())
                .quantity(orderItemDto.getQuantity())
                .receivedQuantity(orderItemDto.receivedQuantity)
                .sentQuantity(orderItemDto.sentQuantity)
                .unitMeasure(orderItemDto.getUnitMeasure())
                .price(orderItemDto.getPrice())
                .isDoneBySupplier(orderItemDto.isDoneBySupplier)
                .isReceived(orderItemDto.isReceived())
                .build();
    }

    public static Set<OrderItemDto> fromEntities(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemDto::fromEntity)
                .collect(Collectors.toSet());
    }

    public static Set<OrderItem> toEntities(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(OrderItemDto::toEntity)
                .collect(Collectors.toSet());
    }

}
