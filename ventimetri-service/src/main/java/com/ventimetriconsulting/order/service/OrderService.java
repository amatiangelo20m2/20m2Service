package com.ventimetriconsulting.order.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.OrderNotFound;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.notification.entity.RedirectPage;
import com.ventimetriconsulting.notification.service.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.dto.CreateOrderEntity;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.entIty.dto.OrderItemDto;
import com.ventimetriconsulting.order.repository.OrderEntityRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
                    .sentQuantity(0)
                    .receivedQuantity(0)
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

        // TODO: send app notification
        // i add a user code to exclude the fcm token from the list
        // so how send the order DONT receive the notification
        List<String> fmcTokensByBranchCode
                = branchUserRepository.findFMCTokensByBranchCode(createOrderEntity.getBranchCodeTarget(),
                createOrderEntity.getUserCode());



        messageSender.enqueMessage(NotificationEntity
                .builder()
                .title("\uD83D\uDCE6 Ordine da " + byBranchCode.getName() +" eseguito da "
                        + createOrderEntity.getUserName())
                .message(productsForNotification.toString())
                .fmcToken(fmcTokensByBranchCode)
                .redirectPage(RedirectPage.ORDERS)
                .build());

        return OrderDTO.toDTO(savedOrder);
    }

    public List<OrderDTO> retrieveOrders(String branchCode,
                                         LocalDate initialDate,
                                         LocalDate endDate,
                                         OrderStatus orderStatus) {


        List<Order> byBranchBranchCodeAndInsertedDateBetween;
        if(orderStatus != null){

            log.info("Retrieve orders for branch with code {} and status in {} between date {} - {}",
                    branchCode,
                    orderStatus,
                    initialDate,
                    endDate);

            byBranchBranchCodeAndInsertedDateBetween = new ArrayList<>(orderEntityRepository.findByBranchBranchCodeAndIncomingDateBetweenAndOrderStatus(branchCode,
                    initialDate,
                    endDate,
                    orderStatus));
        }else{
            log.info("Retrieve orders for branch with code {} between date {} - {}. The status is null so it will retrieved all orders with status different than ARCHIVED", branchCode, initialDate, endDate);
            byBranchBranchCodeAndInsertedDateBetween = orderEntityRepository.findByBranchBranchCodeAndIncomingDateBetweenAndOrderStatusNot(branchCode,
                    initialDate,
                    endDate,
                    OrderStatus.ARCHIVED);
        }

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
    public void updateOrderStatus(long orderId, OrderStatus orderStatus){

        Order order = orderEntityRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        log.info("Update current order {} to [{}]", order, orderStatus);
        order.setOrderStatus(orderStatus);
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
                        orderItem.setReceivedQuantity(orderItemDto.getReceivedQuantity());
                        orderItem.setSentQuantity(orderItemDto.getSentQuantity());
                        orderItem.setUnitMeasure(orderItemDto.getUnitMeasure());
                        orderItem.setPrice(orderItemDto.getPrice());
                        orderItem.setDoneBySupplier(orderItemDto.isDoneBySupplier());
                        orderItem.setReceived(orderItem.isReceived());
                    });
        }

        if(allItemsDoneBySupplier(order.getOrderItems().stream().toList())){
            //TODO SEND NOTIFY TO FACTOTUM (IF PRESENT) OF THIS BRANCH THAT THE ORDER IS READY TO GO
            Optional<BranchUser> byBranchCodeAndRole = branchUserRepository.findByBranchCodeAndRole(order.getCodeTarget(), Role.FACTOTUM);
            byBranchCodeAndRole.ifPresent(branchUser -> messageSender.enqueMessage(
                    NotificationEntity.builder()
                            .title("\uD83D\uDC40 Ordine di" + order.getCreatedByBranchName() +" pronto!")
                            .message("Ordine da consegnare il "
                                    + order.getIncomingDate() + " è pronto a partire! \nProdotti: " + buildProductList(order.getOrderItems()))
                            .redirectPage(RedirectPage.ORDERS)
                            .fmcToken(List.of(branchUser.getFMCToken()))
                            .build()));
        }
        orderEntityRepository.save(order);
    }

    private String buildProductList(Set<OrderItem> orderItems) {

        StringBuilder productsForNotification = new StringBuilder();

        orderItems.forEach((product) -> productsForNotification
                .append("\n - ")
                .append(product.getProductName())
                .append(" ")
                .append(product.getUnitMeasure())
                .append(" ").append(product.getQuantity()));

        return productsForNotification.toString();
    }

    public static boolean allItemsDoneBySupplier(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (!item.isDoneBySupplier()) {
                return false;
            }
        }
        return true;
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
