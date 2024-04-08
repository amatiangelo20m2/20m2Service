package com.ventimetriconsulting.order.entIty.dto;

import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Builder
@ToString
public class OrderDTO {

    private long orderId;
    private String createdBy;
    private LocalDate insertedDate;
    private LocalDate incomingDate;
    private OrderStatus orderStatus;
    Set<OrderItemDto> orderItemDtoList;

    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .createdBy(order.getCreatedBy())
                .insertedDate(order.getInsertedDate())
                .incomingDate(order.getIncomingDate())
                .orderStatus(order.getOrderStatus())
                .orderItemDtoList(OrderItemDto.fromEntities(order.getOrderItems()))
                .build();
    }

    public static Order toEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setCreatedBy(orderDTO.getCreatedBy());
        order.setInsertedDate(orderDTO.getInsertedDate());
        order.setIncomingDate(orderDTO.getIncomingDate());
        order.setOrderStatus(orderDTO.getOrderStatus());
        return order;
    }

    public static List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream().map(OrderDTO::toDTO).collect(Collectors.toList());
    }

    public static List<Order> toEntityList(List<OrderDTO> orderDTOS) {
        return orderDTOS.stream().map(OrderDTO::toEntity).collect(Collectors.toList());
    }
}
