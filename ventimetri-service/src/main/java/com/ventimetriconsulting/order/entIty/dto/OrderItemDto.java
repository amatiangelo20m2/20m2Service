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

    private long orderItemId;
    private long productId;
    private String productName;
    private double quantity;
    private UnitMeasure unitMeasure;
    private double price;

    public static OrderItemDto fromEntity(OrderItem orderItem) {
        return OrderItemDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getOrderItemId())
                .quantity(orderItem.getQuantity())
                .productName(orderItem.getProductName())
                .unitMeasure(orderItem.getUnitMeasure())
                .price(orderItem.getPrice())
                .build();
    }

    public static OrderItem toEntity(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .orderItemId(orderItemDto.getOrderItemId())
                .productName(orderItemDto.getProductName())
                .quantity(orderItemDto.getQuantity())
                .unitMeasure(orderItemDto.getUnitMeasure())
                .price(orderItemDto.getPrice())
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
