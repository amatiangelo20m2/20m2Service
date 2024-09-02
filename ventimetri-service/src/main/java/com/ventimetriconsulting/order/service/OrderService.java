package com.ventimetriconsulting.order.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.OrderNotFound;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.SupplierNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.storage.service.StorageService;
import com.ventimetriconsulting.notification.entity.RedirectPage;
import com.ventimetriconsulting.notification.service.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderItem;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.OrderTarget;
import com.ventimetriconsulting.order.entIty.dto.CreateOrderEntity;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.entIty.dto.OrderItemDto;
import com.ventimetriconsulting.order.repository.OrderEntityRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private OrderEntityRepository orderEntityRepository;
    private BranchRepository branchRepository;
    private SupplierRepository supplierRepository;

    private ProductRepository productRepository;
    private BranchUserRepository branchUserRepository;
    private MessageSender messageSender;

    private StorageService storageService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Transactional
    public OrderDTO createOrder(CreateOrderEntity createOrderEntity) {

        log.info("Creating order by user {} (with code {}). This order is by branch with code {} for a branch/supplier with code {}/{}. " +
                        "The order is requested to be delivered in {} to receive in preferred hour ({}). Set in CONSEGNATO STATUS? [{}]",
                createOrderEntity.getUserName(),
                createOrderEntity.getUserCode(),
                createOrderEntity.getBranchCode(),
                createOrderEntity.getBranchCodeTarget(),
                createOrderEntity.getSupplierCodeTarget(),
                createOrderEntity.getIncomingDate(),
                createOrderEntity.getPreferredReceivingHour(),
                createOrderEntity.isThisOrderAlreadyInConsegnatoStatus());

        Branch byBranchCode = branchRepository.findByBranchCode(createOrderEntity.getBranchCode())
                .orElseThrow(() -> new BranchNotFoundException("Exception throwed while getting data. No branch with code : " + createOrderEntity.getBranchCode() + "found. Cannot create order for the branch" ));

        String targetName = "";
        String targetCode = "";
        if(OrderTarget.BRANCH == createOrderEntity.getOrderTarget()) {
            targetName = branchRepository
                    .findBranchNameByBranchCode(createOrderEntity.getBranchCodeTarget()).orElseThrow(() -> new BranchNotFoundException("Exception throwed while getting data. No branch with code  : " + createOrderEntity.getBranchCodeTarget() + "found. Cannot retrieve branch name" ));
            targetCode = createOrderEntity.getBranchCodeTarget();
        }else if(OrderTarget.SUPPLIER == createOrderEntity.getOrderTarget()){
            log.info("Retrieve supplier with code {}", createOrderEntity.getSupplierCodeTarget());
            targetName = supplierRepository.findSupplierNameByCode(createOrderEntity.getSupplierCodeTarget())
                    .orElseThrow(() -> new SupplierNotFoundException("Exception throwed while getting data. No supplier found with code  : " + createOrderEntity.getSupplierCodeTarget() + "found. Cannot retrieve supplier name" ));
            targetCode = createOrderEntity.getSupplierCodeTarget();
        }

        Order savedOrder = orderEntityRepository.save(Order.builder()
                .orderId(0L)
                .branch(byBranchCode)
                .userCode(createOrderEntity.getUserCode())
                .createdByUser(createOrderEntity.getUserName())
                .createdByBranchName(byBranchCode.getName())
                .insertedDate(LocalDate.parse(createOrderEntity.getInsertedDate(), formatter))
                .nameTarget(targetName)
                .codeTarget(targetCode)
                .incomingDate(LocalDate.parse(createOrderEntity.getIncomingDate(), formatter))
                .preferredReceivingHour(getHourInLocalTimeFormat(createOrderEntity.getPreferredReceivingHour()))
                .orderTarget(createOrderEntity.getOrderTarget())
                .orderStatus(OrderStatus.BOZZA)
                .build());

        log.info("Add to order created in a DRAFT satus the product: ");

        StringBuilder productsForNotification = new StringBuilder();

        createOrderEntity.getOrderItemAmountMap().forEach((prodId, prodAmount) -> {

            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new ProductNotFoundException("Exception throwed while getting data. No product found with id  : " + prodId + ". Cannot create order." ));

            log.info(" - {} x {} {}", product.getName(), prodAmount , product.getUnitMeasure());
            productsForNotification
                    .append("\n - ")
                    .append(prodAmount.toString())
                    .append(" ")
                    .append(product.getUnitMeasure())
                    .append(" ").append(product.getName());

            // if the target is supplier set received qantity and sent quanty the same as the quantity requested. The user just need to confirm it
            if(OrderTarget.SUPPLIER == createOrderEntity.getOrderTarget()){
                savedOrder.getOrderItems().add(OrderItem.builder()
                        .productId(product.getProductId())
                        .sentQuantity(prodAmount)
                        .receivedQuantity(prodAmount)
                        .productName(product.getName())
                        .quantity(prodAmount)
                        .price(product.getPrice())
                        .vat(product.getVatApplied())
                        .vatPrice(product.getVatPrice())
                        .isReceived(false)
                        .isDoneBySupplier(true)
                        .unitMeasure(product.getUnitMeasure())
                        .build());
            }else{
                savedOrder.getOrderItems().add(OrderItem.builder()
                        .productId(product.getProductId())
                        .sentQuantity(0)
                        .receivedQuantity(0)
                        .productName(product.getName())
                        .quantity(prodAmount)
                        .price(product.getPrice())
                        .vatPrice(product.getVatPrice())
                        .vat(product.getVatApplied())
                        .isReceived(false)
                        .isDoneBySupplier(false)
                        .unitMeasure(product.getUnitMeasure())
                        .build());
            }
        });
        log.info("Save order with status SENT");

        if(OrderTarget.BRANCH == createOrderEntity.getOrderTarget()){
            savedOrder.setOrderStatus(OrderStatus.IN_LAVORAZIONE);

            List<BranchUser> branchUsers
                    = branchUserRepository.findFMCTokensByBranchCode(createOrderEntity.getBranchCodeTarget(),
                    createOrderEntity.getUserCode());


            branchUsers.forEach((branchUser)-> {
                messageSender.enqueMessage(NotificationEntity
                        .builder()
                        .title("\uD83D\uDCE6 Ordine da " + byBranchCode.getName() +" eseguito da " + createOrderEntity.getUserName())
                        .message(productsForNotification.toString())
                        .fmcToken(branchUser.getFMCToken())
                        .branchCode(branchUser.getBranch().getBranchCode())
                        .userCode(branchUser.getUserCode())
                        .redirectPage(RedirectPage.ORDERS)
                        .build());
            });

        }else if(OrderTarget.SUPPLIER == createOrderEntity.getOrderTarget()){

            if(createOrderEntity.isThisOrderAlreadyInConsegnatoStatus()){
                log.info("Save the order already in DA_CONFERMARE status while is been executed in the same day than the day required to be delivered.");
                savedOrder.setOrderStatus(OrderStatus.DA_CONFERMARE);
            }else{
                log.info("Save the order for supplier with code {} in INVIATO status", createOrderEntity.getSupplierCodeTarget());
                savedOrder.setOrderStatus(OrderStatus.INVIATO);
            }
        }
        return OrderDTO.toDTO(savedOrder);
    }

    public static LocalTime getHourInLocalTimeFormat(String preferredReceivingHour) {
        try {
            // Define the expected time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Parse the string to LocalTime
            return LocalTime.parse(preferredReceivingHour, formatter);
        } catch (DateTimeParseException e) {
            // Handle the case where the input string is not in the expected format
            log.error("Invalid time format: " + preferredReceivingHour + ". The format must be hh:mm");
            return null;
        }
    }

    @Transactional
    @Modifying
    public void retrieveSupplierOrderByDateStillToUpdateToConsegnato(){
        log.info("Retrieve all orders in INVIATO status for All Suppliers. Change the status to CONSEGNATO in order to make possible to move into storage");
        List<Order> byOrderTargetAndIncomingDateAndOrderStatus = orderEntityRepository
                .findByOrderTargetAndIncomingDateAndOrderStatus(OrderTarget.SUPPLIER,
                        LocalDate.now(),
                        OrderStatus.INVIATO);
        List<OrderDTO> dtoList = OrderDTO.toDTOList(byOrderTargetAndIncomingDateAndOrderStatus);

        log.info(dtoList.toString());


        for(Order order : byOrderTargetAndIncomingDateAndOrderStatus){
            log.info("Updating order to CONSEGNATO with id " + order.getOrderId());
            order.setOrderStatus(OrderStatus.DA_CONFERMARE);
        }
    }

    public List<OrderDTO> retrieveOrdersByStatus(String branchCode, OrderStatus orderStatus){
        log.info("Retrieve orders for branch with code {} and status in {}",
                branchCode,
                orderStatus);
        List<Order> byBranchBranchCodeAndOrderStatus = orderEntityRepository.findByBranchBranchCodeAndOrderStatus(branchCode, orderStatus);

        if(byBranchBranchCodeAndOrderStatus.isEmpty()){
            log.warn("No orders found for branch with code {} in status {}", branchCode, orderStatus);
            return new ArrayList<>();
        }else{
            return OrderDTO.toDTOList(byBranchBranchCodeAndOrderStatus);
        }
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
                    OrderStatus.ARCHIVIATO);
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
    @Modifying
    public void updateOrderStatus(long orderId, OrderStatus orderStatus){

        Order order = orderEntityRepository.findById(orderId).orElseThrow(()
                -> new IllegalArgumentException("Order not found with id " + orderId));
        log.info("Update current order {} to [{}]", order, orderStatus);
        order.setOrderStatus(orderStatus);
    }

    @Transactional
    @Modifying
    public void updateOrder(long orderId,
                            List<OrderItemDto> orderItemDtos,
                            OrderStatus status,
                            long storageId,
                            String userName) {

        Order order = orderEntityRepository.findById(orderId).orElseThrow(()
                -> new IllegalArgumentException("Order not found"));

        log.info("Update Order with id {} to status {}", orderId, status);

        for(OrderItemDto orderItemDto : orderItemDtos){

            log.info("Update Order item - " +
                            "ProdId {}, " +
                            "product name {}, " +
                            "quantity {}, " +
                            "unitMeasure {}, " +
                            "price {}, Received? {}", orderItemDto.getProductId(),
                    orderItemDto.getProductName(),
                    orderItemDto.getQuantity(),
                    orderItemDto.getUnitMeasure().name(),
                    orderItemDto.getPrice(),
                    orderItemDto.isReceived());

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

        //if the status is in consegnato the products will be removed from the origin storage
        if(OrderStatus.CONSEGNATO == status) {
            log.info("Upon consegnato order the system will remove the products from the storage with id {}", storageId);

            Map<Long, Double> removeProdMap = new HashMap<>();

            order.getOrderItems().forEach(orderItem -> {
                removeProdMap.put(orderItem.getProductId(), orderItem.getSentQuantity());
            });

            storageService.removeProductAmountFromStorage(
                    removeProdMap,
                    storageId,
                    userName);


        }


        //if the status is in archiviato the products will be added into destination storage
        if(OrderStatus.ARCHIVIATO == status) {
            log.info("Upon archivied order the system will add the products into the storage with id {}", storageId);
            for(OrderItem orderItem : order.getOrderItems()) {
                storageService.insertProductToStorage(orderItem.getProductId(), storageId, userName, orderItem.getReceivedQuantity());
            }
        }

        if(allItemsDoneBySupplier(order.getOrderItems().stream().toList())){
            order.setOrderStatus(status);
            orderEntityRepository.save(order);
            //TODO SEND NOTIFY TO FACTOTUM (IF PRESENT) OF THIS BRANCH THAT THE ORDER IS READY TO GO
            Optional<BranchUser> byBranchCodeAndRole = branchUserRepository.findByBranchCodeAndRole(order.getCodeTarget(), Role.FACTOTUM);

            if (Objects.requireNonNull(status) == OrderStatus.INVIATO) {

                byBranchCodeAndRole.ifPresent(branchUser -> Collections.singletonList(branchUser.getFMCToken()).forEach((token) -> {

                    messageSender.enqueMessage(
                            NotificationEntity.builder()
                                    .title("\uD83D\uDC40 Ordine di" + order.getCreatedByBranchName() + " pronto!")
                                    .message("Ordine da consegnare il "
                                            + order.getIncomingDate() + " è pronto a partire! \nProdotti: " + buildProductList(order.getOrderItems()))
                                    .redirectPage(RedirectPage.ORDERS)
                                    .fmcToken(token)
                                    .userCode(branchUser.getUserCode())
                                    .branchCode(branchUser.getBranch().getBranchCode())
                                    .build());
                }));

            }else if(Objects.requireNonNull(status) == OrderStatus.PRONTO_A_PARTIRE) {

                byBranchCodeAndRole.ifPresent(branchUser -> Collections.singletonList(branchUser.getFMCToken()).forEach((token) -> {

                    messageSender.enqueMessage(
                            NotificationEntity.builder()
                                    .title("\uD83D\uDC40 Ordine di" + order.getCreatedByBranchName() + " è pronto a partire!")
                                    .message("Ordine da consegnare il "
                                            + order.getIncomingDate() + " è pronto a partire! \nProdotti: " + buildProductList(order.getOrderItems()))
                                    .redirectPage(RedirectPage.ORDERS)
                                    .fmcToken(token)
                                    .userCode(branchUser.getUserCode())
                                    .branchCode(branchUser.getBranch().getBranchCode())
                                    .build());
                }));

            } else if(Objects.requireNonNull(status) == OrderStatus.CONSEGNATO) {

                byBranchCodeAndRole.ifPresent(branchUser -> Collections.singletonList(branchUser.getFMCToken()).forEach((token) -> {

                    messageSender.enqueMessage(
                            NotificationEntity.builder()
                                    .title("\uD83D\uDC40 Ordine di" + order.getCreatedByBranchName() + " consegnato!")
                                    .message("Ordine consegnato. Recap ordine - \nProdotti: " + buildProductList(order.getOrderItems()))
                                    .redirectPage(RedirectPage.ORDERS)
                                    .fmcToken(token)
                                    .userCode(branchUser.getUserCode())
                                    .branchCode(branchUser.getBranch().getBranchCode())
                                    .build());
                }));
            }

        }
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
    public void deleteOrderItem(long orderId, List<Long> productIdList) {
        log.info("Delete order items with product ids {} from order with id {}", productIdList, orderId);

        Order order = orderEntityRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.getOrderItems().removeIf(orderItem -> productIdList.contains(orderItem.getProductId()));
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

    @Transactional
    @Modifying
    public OrderDTO editOrder(long orderId, Map<Long, Double> requestEditingMap) {

        log.info("Edit order with id {} and assign this new values {}", orderId, requestEditingMap);
        Order order = orderEntityRepository.findById(orderId).orElseThrow(() ->
                new OrderNotFound("Exception thrown while getting data. No order for id: " + orderId + " found."));

        Set<OrderItem> orderItems = order.getOrderItems();

        Iterator<OrderItem> iterator = orderItems.iterator();
        while (iterator.hasNext()) {
            OrderItem orderItem = iterator.next();
            if (requestEditingMap.containsKey(orderItem.getProductId())) {
                double newQuantity = requestEditingMap.get(orderItem.getProductId());
                if (newQuantity == 0) {
                    iterator.remove();
                } else {
                    orderItem.setQuantity(newQuantity);
                }
            }
        }

        // Save the order back to the repository
        Order savedOrder = orderEntityRepository.save(order);

        // Return the updated order as a DTO
        return OrderDTO.toDTO(savedOrder);


    }
}
