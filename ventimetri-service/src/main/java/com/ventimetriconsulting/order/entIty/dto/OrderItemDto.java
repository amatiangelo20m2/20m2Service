package com.ventimetriconsulting.order.entIty.dto;

import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
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
        TreeSet<OrderItemDto> sortedOrderItems = new TreeSet<>(Comparator.comparing(o -> o.getProductName().toLowerCase()));
        orderItems.forEach(orderItem -> sortedOrderItems.add(OrderItemDto.fromEntity(orderItem)));
        return sortedOrderItems;
    }


    public static Set<OrderItem> toEntities(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(OrderItemDto::toEntity)
                .collect(Collectors.toSet());
    }

}
