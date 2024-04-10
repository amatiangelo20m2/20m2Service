package com.ventimetriconsulting.order.entIty.dto;

import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.OrderTarget;
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
    private String createdByUser;
    private String createdBranchCode;
    private String createdBranchName;

    private LocalDate insertedDate;
    private LocalDate incomingDate;
    private OrderStatus orderStatus;

    private OrderTarget orderTarget;
    private String codeTarget;
    private String nameTarget;


    Set<OrderItemDto> orderItemDtoList;

    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .createdBranchCode(order.getBranch().getBranchCode())
                .createdBranchName(order.getBranch().getName())
                .createdByUser(order.getCreatedByUser())
                .codeTarget(order.getCodeTarget())
                .nameTarget(order.getNameTarget())
                .orderTarget(order.getOrderTarget())
                .insertedDate(order.getInsertedDate())
                .incomingDate(order.getIncomingDate())
                .orderStatus(order.getOrderStatus())
                .orderItemDtoList(OrderItemDto.fromEntities(order.getOrderItems()))
                .build();
    }

//    public static Order toEntity(OrderDTO orderDTO) {
//        Order order = new Order();
//        order.setOrderId(orderDTO.getOrderId());
//        order.setCreatedBy(orderDTO.getCreatedBy());
//        order.setInsertedDate(orderDTO.getInsertedDate());
//        order.setOrderTarget(orderDTO.getOrderTarget());
//        order.setBranchNameTarget(orderDTO.getBranchNameTarget());
//        order.setSupplierNameTarget(orderDTO.getSupplierNameTarget());
//        order.setBranchCodeTarget(orderDTO.getBranchCodeTarget());
//        order.setSupplierCodeTarget(orderDTO.getSupplierCodeTarget());
//        order.setIncomingDate(orderDTO.getIncomingDate());
//        order.setOrderStatus(orderDTO.getOrderStatus());
//        return order;
//    }

    public static List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream().map(OrderDTO::toDTO).collect(Collectors.toList());
    }

//    public static List<Order> toEntityList(List<OrderDTO> orderDTOS) {
//        return orderDTOS.stream().map(OrderDTO::toEntity).collect(Collectors.toList());
//    }
}
