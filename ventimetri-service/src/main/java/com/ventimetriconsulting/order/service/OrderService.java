package com.ventimetriconsulting.order.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.OrderNotFound;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.dto.CreateOrderEntity;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.entIty.dto.OrderItemDto;
import com.ventimetriconsulting.order.repository.OrderItemRepository;
import com.ventimetriconsulting.order.repository.OrderEntityRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private OrderEntityRepository orderEntityRepository;
    private OrderItemRepository orderItemRepository;
    private BranchRepository branchRepository;
    private ProductRepository productRepository;

    @Transactional
    public OrderDTO createOrder(CreateOrderEntity createOrderEntity) {

        log.info("Creating order by user {} (with code {}) for a branch/supplier with code {}/{}",
                createOrderEntity.getUserName(),
                createOrderEntity.getUserCode(),
                createOrderEntity.getBranchCodeTarget(),
                createOrderEntity.getSupplierCodeTarget());

        Branch byBranchCode = branchRepository.findByBranchCode(createOrderEntity.getBranchCode())
                .orElseThrow(() -> new BranchNotFoundException("Exception thowed while getting data. No branch with code  : " + createOrderEntity.getBranchCode() + "found. Cannot create order for the branch" ));

        Order order = Order.builder()
                .orderId(0L)
                .branch(byBranchCode)
                .userCode(createOrderEntity.getUserCode())
                .createdBy(createOrderEntity.getUserName())
                .insertedDate(createOrderEntity.getInsertedDate())
                .incomingDate(createOrderEntity.getIncomingDate())
                .orderTarget(createOrderEntity.getOrderTarget())
                .branchCodeTarget(createOrderEntity.getBranchCodeTarget())
                .supplierCodeTarget(createOrderEntity.getSupplierCodeTarget())
                .orderStatus(OrderStatus.DRAFT)
                .build();

        Order savedOrder = orderEntityRepository.save(order);

        log.info("Add to order created in a DRAFT satus the product: ");

        createOrderEntity.getOrderItemAmountMap().forEach((prodId, prodAmount) -> {

            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new ProductNotFoundException("Exception thowed while getting data. No product found with id  : " + prodId + ". Cannot create order." ));

            log.info(" - {} x {} {}", product.getName(), prodAmount, product.getUnitMeasure());


            OrderItem save = orderItemRepository.save(
                    OrderItem.builder()
                            .orderItemId(0L)
                            .productId(String.valueOf(product.getProductId()))
                            .productName(product.getName())
                            .quantity(prodAmount)
                            .price(product.getPrice())
                            .unitMeasure(product.getUnitMeasure())
                            .build());
            savedOrder.getOrderItems().add(save);

        });

        log.info("Save order with status SENT");

        //TODO: send app notification
        savedOrder.setOrderStatus(OrderStatus.CREATED);

//        Order orderAfterSaving = orderEntityRepository.findById(
//                savedOrder.getOrderId()).orElseThrow(() -> new OrderNotFound("Exception thowed while getting data. No order found with id  : " + savedOrder.getOrderId() + ". Cannot retrieve order."));

        return OrderDTO.toDTO(savedOrder);
    }

    public List<OrderDTO> retrieveOrders(String branchCode, LocalDate initialDate, LocalDate endDate) {

        log.info("Retrieve orders for branch with code {} between date {} - {}", branchCode, initialDate, endDate);

        List<Order> byBranchBranchCodeAndInsertedDateBetween = orderEntityRepository.findByBranchBranchCodeAndInsertedDateBetween(branchCode, initialDate, endDate);
        return OrderDTO.toDTOList(byBranchBranchCodeAndInsertedDateBetween);
    }
}
