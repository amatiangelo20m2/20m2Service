package com.ventimetriconsulting.order.controller;

import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.dto.*;
import com.ventimetriconsulting.order.service.OrderService;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/order/")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private OrderService orderService;

    @PostMapping(path = "/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderEntity createOrderEntity){
        OrderDTO orderDTO = orderService.createOrder(createOrderEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(orderDTO);
    }

    @GetMapping(path = "/retrieve")
    public ResponseEntity<List<OrderDTO>> getOrderByBrancCode(
            @RequestParam String branchCode,
            @RequestParam String startDate,
            @RequestParam String endDate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateLocalDate = LocalDate.parse(startDate, formatter);
        LocalDate endDateLocalDate = LocalDate.parse(endDate, formatter);

        if (StringUtils.isEmpty(branchCode)) {
            return ResponseEntity.badRequest().body(null);
        }

        if (startDateLocalDate.isAfter(endDateLocalDate)) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            List<OrderDTO> listOrderDtos = orderService
                    .retrieveOrders(branchCode,
                            startDateLocalDate,
                            endDateLocalDate,
                            null);

            return ResponseEntity.ok().body(listOrderDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(path = "/retrievearchivedorders")
    public ResponseEntity<List<OrderDTO>> getOrderArchivedByBrancCode(
            @RequestParam String branchCode,
            @RequestParam String startDate,
            @RequestParam String endDate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateLocalDate = LocalDate.parse(startDate, formatter);
        LocalDate endDateLocalDate = LocalDate.parse(endDate, formatter);

        if (StringUtils.isEmpty(branchCode)) {
            return ResponseEntity.badRequest().body(null);
        }

        if (startDateLocalDate.isAfter(endDateLocalDate)) {
            return ResponseEntity.badRequest().body(null);
        }


        try {
            List<OrderDTO> listOrderDtos = orderService
                    .retrieveOrders(branchCode,
                            startDateLocalDate,
                            endDateLocalDate,
                            OrderStatus.ARCHIVIATO);

            return ResponseEntity.ok().body(listOrderDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(path = "/updatetoprontoapartire")
    public ResponseEntity<OrderDTO> updateOrder(@RequestParam long orderId,
                                                @RequestBody List<OrderItemDto> orderItemDtoList) {
        try {
            orderService.updateOrderItem(orderId, orderItemDtoList,
                    OrderStatus.PRONTO_A_PARTIRE, 0L, null);
            return ResponseEntity.status(HttpStatus.OK).body(orderService.retrieveOrderByOrderId(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(path = "/updatetoconsegnato")
    public ResponseEntity<OrderDTO> updateOrderToDelivered(@RequestParam long orderId,
                                                           @RequestBody List<OrderItemDto> orderItemDtoList) {
        try {
            orderService.updateOrderItem(orderId, orderItemDtoList, OrderStatus.CONSEGNATO, 0L, null);
            return ResponseEntity.status(HttpStatus.OK).body(orderService.retrieveOrderByOrderId(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping(path = "/updatetoarchiviato")
    public ResponseEntity<OrderDTO> updateOrderToArchived(@RequestParam long orderId,
                                                          @RequestBody List<OrderItemDto> orderItemDtoList,
                                                          @RequestParam  long storageId,
                                                          @RequestParam String userName) {
        try {
            orderService.updateOrderItem(orderId, orderItemDtoList, OrderStatus.ARCHIVIATO, storageId, userName);

            return ResponseEntity.status(HttpStatus.OK).body(orderService.retrieveOrderByOrderId(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping(path = "/updatestatus")
    public ResponseEntity<OrderDTO> updateOrderStatus(@RequestParam long orderId,
                                                      @RequestParam OrderStatus orderStatus) {
        try {
            orderService.updateOrderStatus(orderId, orderStatus);

            return ResponseEntity.status(HttpStatus.OK).body(orderService.retrieveOrderByOrderId(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping(path = "/deleteorderitem")
    public ResponseEntity<Void> deleteOrderItemFromOrder(@RequestParam long orderId, @RequestParam long productId) {
        try {
            orderService.deleteOrderItem(orderId, productId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(path = "/deleteorder")
    public ResponseEntity<Void> deleteOrder(@RequestParam long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/retrievesingleproddetailsforbranchordersandrangedate")
    public ResponseEntity<Map<LocalDate, OrderItemDto>> retrieveSingleProductDetailsForRangeDateOrders(
            @RequestParam long productId,
            @RequestParam String branchCode,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        log.info("Retrieve all history details for the product " +
                " with id {} in the orders of branch with code {} in" +
                " the date range between {} and {}", productId, branchCode, startDate, endDate);
        List<OrderDTO> orderArchivedByBrancCode = getOrderArchivedByBrancCode(branchCode, startDate, endDate).getBody();

        Map<LocalDate, OrderItemDto> resultMap = new HashMap<>();

        for (OrderDTO order : Objects.requireNonNull(orderArchivedByBrancCode)) {
            for (OrderItemDto item : order.getOrderItemDtoList()) {
                if (item.getProductId() == productId) {
                    LocalDate incomingDate = order.getIncomingDate();
                    OrderItemDto existingItem = resultMap.get(incomingDate);
                    if (existingItem != null) {
                        // If the key is already present, update the existing item
                        existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                        existingItem.setReceivedQuantity(existingItem.getReceivedQuantity() + item.getReceivedQuantity());
                        existingItem.setSentQuantity(existingItem.getSentQuantity() + item.getSentQuantity());
                    } else {
                        // If the key is not present, add a new item to the map
                        resultMap.put(incomingDate, new OrderItemDto(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getReceivedQuantity(),
                                item.getSentQuantity(),
                                item.getUnitMeasure(),
                                item.getPrice(),
                                item.isDoneBySupplier(),
                                item.isReceived()
                        ));
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    @GetMapping(path = "/retrieveexceldatafromarchiviedorders")
    public ResponseEntity<OrderResultRecap> retrieveExcelDataFromArchiviedOrders(
            @RequestParam String branchCode,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        log.info("Retrieve archived orders data for excel for branch with " +
                "code {} between date {} and {}", branchCode, startDate, endDate);

        List<OrderDTO> orderArchivedByBrancCode = getOrderArchivedByBrancCode(branchCode, startDate, endDate).getBody();

        List<OrderDTO> incomingOrders
                = new ArrayList<>();
        List<OrderDTO> outgoingOrders
                = new ArrayList<>();

        for (OrderDTO orderDTO : Objects.requireNonNull(orderArchivedByBrancCode)) {
            if (Objects.equals(orderDTO.getCodeTarget(), branchCode)) {
                outgoingOrders.add(orderDTO);
            } else {
                incomingOrders.add(orderDTO);
            }
        }

        List<OrderResultRecap.DetailedProductRecap> incomingRecap = categorizeOrders(incomingOrders);
        List<OrderResultRecap.DetailedProductRecap> outgoingRecap = categorizeOrders(outgoingOrders);

        OrderResultRecap orderResultRecap = OrderResultRecap.builder()
                .incomingsOrders(incomingRecap)
                .outgoingOrders(outgoingRecap)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderResultRecap);
    }

    private List<OrderResultRecap.DetailedProductRecap> categorizeOrders(List<OrderDTO> orders) {
        Map<String, OrderResultRecap.DetailedProductRecap> recapMap = new HashMap<>();

        for (OrderDTO order : orders) {
            String code = order.getCodeTarget();
            String name = order.getNameTarget();

            OrderResultRecap.DetailedProductRecap recap =
                    recapMap.computeIfAbsent(code, k -> new OrderResultRecap.DetailedProductRecap(code, name, new ArrayList<>()));

            Map<Long, ExcelDataArchivedOrder> productMap = recap.getExcelDataArchivedOrderList().stream()
                    .collect(Collectors.toMap(ExcelDataArchivedOrder::getProductId, product -> product));

            for (OrderItemDto orderItemDto : order.getOrderItemDtoList()) {
                long productId = orderItemDto.getProductId();
                String productName = orderItemDto.getProductName();
                double quantity = orderItemDto.getQuantity();
                double receivedQuantity = orderItemDto.getReceivedQuantity();
                double sentQuantity = orderItemDto.getSentQuantity();
                UnitMeasure unitMeasure = orderItemDto.getUnitMeasure();
                double price = orderItemDto.getPrice();

                ExcelDataArchivedOrder excelDataArchivedOrder = productMap.get(productId);
                if (excelDataArchivedOrder == null) {
                    excelDataArchivedOrder = new ExcelDataArchivedOrder();
                    excelDataArchivedOrder.setProductId(productId);
                    excelDataArchivedOrder.setProductName(productName);
                    excelDataArchivedOrder.setUnitMeasure(unitMeasure);
                    excelDataArchivedOrder.setPrice(price);
                    excelDataArchivedOrder.setQuantity(quantity);
                    excelDataArchivedOrder.setReceivedQuantity(receivedQuantity);
                    excelDataArchivedOrder.setSentQuantity(sentQuantity);
                    productMap.put(productId, excelDataArchivedOrder);
                    recap.getExcelDataArchivedOrderList().add(excelDataArchivedOrder);
                } else {
                    excelDataArchivedOrder.setQuantity(excelDataArchivedOrder.getQuantity() + quantity);
                    excelDataArchivedOrder.setReceivedQuantity(excelDataArchivedOrder.getReceivedQuantity() + receivedQuantity);
                    excelDataArchivedOrder.setSentQuantity(excelDataArchivedOrder.getSentQuantity() + sentQuantity);
                }
            }

            List<ExcelDataArchivedOrder> sortedList = sortByProductNameDesc(new ArrayList<>(productMap.values()));
            recap.setExcelDataArchivedOrderList(sortedList);
        }
        return new ArrayList<>(recapMap.values());
    }


    public static List<ExcelDataArchivedOrder> sortByProductNameDesc(Collection<ExcelDataArchivedOrder> collection) {
        return collection.stream()
                .sorted(Comparator.comparing(o -> o.getProductName().toLowerCase()))
                .collect(Collectors.toList());
    }


}
