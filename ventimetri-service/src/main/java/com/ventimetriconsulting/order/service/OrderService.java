package com.ventimetriconsulting.order.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.OrderNotFound;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.notification.entity.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.dto.CreateOrderEntity;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.entIty.dto.OrderItemDto;
import com.ventimetriconsulting.order.repository.OrderEntityRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private OrderEntityRepository orderEntityRepository;
    private BranchRepository branchRepository;
    private ProductRepository productRepository;
    private BranchUserRepository branchUserRepository;
    private MessageSender messageSender;

    @Transactional
    public OrderDTO createOrder(CreateOrderEntity createOrderEntity) {

        log.info("Creating order by user {} (with code {}) for a branch/supplier with code {}/{}",
                createOrderEntity.getUserName(),
                createOrderEntity.getUserCode(),
                createOrderEntity.getBranchCodeTarget(),
                createOrderEntity.getSupplierCodeTarget());

        Branch byBranchCode = branchRepository.findByBranchCode(createOrderEntity.getBranchCode())
                .orElseThrow(() -> new BranchNotFoundException("Exception thowed while getting data. No branch with code : " + createOrderEntity.getBranchCode() + "found. Cannot create order for the branch" ));

        String branchTargetName = branchRepository.findBranchNameByBranchCode(createOrderEntity.getBranchCodeTarget()).orElseThrow(() -> new BranchNotFoundException("Exception thowed while getting data. No branch with code  : " + createOrderEntity.getBranchCodeTarget() + "found. Cannot retrieve branch name" ));;

        Order savedOrder = orderEntityRepository.save(Order.builder()
                .orderId(0L)
                .branch(byBranchCode)
                .userCode(createOrderEntity.getUserCode())
                .createdByUser(createOrderEntity.getUserName())
                .createdByBranchName(byBranchCode.getName())
                .insertedDate(createOrderEntity.getInsertedDate())

                .nameTarget(branchTargetName)
                .codeTarget(createOrderEntity.getBranchCodeTarget())

                .incomingDate(createOrderEntity.getIncomingDate())
                .orderTarget(createOrderEntity.getOrderTarget())
                .orderStatus(OrderStatus.DRAFT)
                .build());

        log.info("Add to order created in a DRAFT satus the product: ");

        StringBuilder productsForNotification = new StringBuilder();

        createOrderEntity.getOrderItemAmountMap().forEach((prodId, prodAmount) -> {

            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new ProductNotFoundException("Exception thowed while getting data. No product found with id  : " + prodId + ". Cannot create order." ));

            log.info(" - {} x {} {}", product.getName(), prodAmount , product.getUnitMeasure());
            productsForNotification
                    .append("\n - ")
                    .append(prodAmount.toString())
                    .append(" ")
                    .append(product.getUnitMeasure())
                    .append(" ").append(product.getName());

            savedOrder.getOrderItems().add(OrderItem.builder()
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .quantity(prodAmount)
                    .price(product.getPrice())
                    .isReceived(false)
                    .isDoneBySupplier(false)
                    .unitMeasure(product.getUnitMeasure())
                    .build());
        });

        log.info("Save order with status SENT");

        savedOrder.setOrderStatus(OrderStatus.CREATED);

        //TODO: send app notification

        List<String> fmcTokensByBranchCode
                = branchUserRepository.findFMCTokensByBranchCode(createOrderEntity.getBranchCodeTarget());

        messageSender.enqueMessage(NotificationEntity
                .builder()
                .title("\uD83D\uDCE6 Ordine da " + byBranchCode.getName() +" eseguito da " + createOrderEntity.getUserName())
                .message(productsForNotification.toString())
                .fmcToken(fmcTokensByBranchCode)
                .notificationType(NotificationEntity.NotificationType.IN_APP_NOTIFICATION)
                .build());

        return OrderDTO.toDTO(savedOrder);
    }

    public List<OrderDTO> retrieveOrders(String branchCode,
                                         LocalDate initialDate,
                                         LocalDate endDate) {

        log.info("Retrieve orders for branch with code {} between date {} - {}", branchCode, initialDate, endDate);

        List<Order> byBranchBranchCodeAndInsertedDateBetween = orderEntityRepository.findByBranchBranchCodeAndIncomingDateBetween(branchCode, initialDate, endDate);

        log.info("Found n {} orders for branch with code {}", byBranchBranchCodeAndInsertedDateBetween.size(), branchCode);

        log.info("Retrieve orders where this branch code {} is a target branch. Orders between date {} - {}", branchCode, initialDate, endDate);
        byBranchBranchCodeAndInsertedDateBetween.addAll(orderEntityRepository.findByCodeTargetAndIncomingDateBetween(branchCode, initialDate, endDate));

        log.info("Found n {} orders for this branch (as target) with code {}", byBranchBranchCodeAndInsertedDateBetween.size(), branchCode);

        return new ArrayList<>(OrderDTO.toDTOList(byBranchBranchCodeAndInsertedDateBetween).stream()
                .collect(Collectors.toMap(
                        OrderDTO::getOrderId, // Key by orderId
                        orderDTO -> orderDTO, // Values are the OrderDTO objects themselves
                        (existing, replacement) -> existing)) // In case of key collision, keep the existing value
                .values());
    }

    @Transactional
    public void updateOrderItem(long orderId,
                                List<OrderItemDto> orderItemDtos) {



        Order order = orderEntityRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        for(OrderItemDto orderItemDto : orderItemDtos){

            log.info("Update Order with id {}, " +
                    "ProdId {}, " +
                    "product name {}, " +
                    "quantity {}, " +
                    "unitMeasure {}, " +
                    "price {}", orderId, orderItemDto.getProductId(),
                    orderItemDto.getProductName(),
                    orderItemDto.getQuantity(),
                    orderItemDto.getUnitMeasure().name(),
                    orderItemDto.getPrice());

            order.getOrderItems().stream()
                    .filter(orderItem -> orderItem.getProductId() == orderItemDto.getProductId())
                    .findFirst()
                    .ifPresent(orderItem -> {
                        orderItem.setProductName(orderItemDto.getProductName());
                        orderItem.setQuantity(orderItemDto.getQuantity());
                        orderItem.setUnitMeasure(orderItemDto.getUnitMeasure());
                        orderItem.setPrice(orderItemDto.getPrice());
                        orderItem.setDoneBySupplier(orderItemDto.isDoneBySupplier());
                        orderItem.setReceived(orderItem.isReceived());
                    });
        }

        orderEntityRepository.save(order);
    }

    @Transactional
    @Modifying
    public void deleteOrderItem(long orderId, long productId) {
        log.info("Delete order item with product id {} from order with id {}",productId, orderId);

        Order order = orderEntityRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.getOrderItems().removeIf(orderItem -> orderItem.getProductId() == productId);
        orderEntityRepository.save(order);
    }
    @Transactional
    @Modifying
    public void deleteOrder(long orderId) {
        log.info("Delete order with id {}", orderId);
        orderEntityRepository.deleteById(orderId);
    }

    public OrderDTO retrieveOrderByOrderId(long orderId) {
        Order order = orderEntityRepository.findById(orderId).orElseThrow(()
                -> new OrderNotFound("Exception trowed while getting data. No order for id : " + orderId + " found."));
        return OrderDTO.toDTO(order);
    }
}
