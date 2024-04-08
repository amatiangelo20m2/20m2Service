package com.ventimetriconsulting.order.controller;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.dto.VentiMetriQuadriData;
import com.ventimetriconsulting.order.entIty.dto.CreateOrderEntity;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.entIty.dto.RetrieveOrderEntity;
import com.ventimetriconsulting.order.service.OrderService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/order/")
@AllArgsConstructor

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
            @RequestBody RetrieveOrderEntity retrieveOrderEntity){

        if (StringUtils.isEmpty(retrieveOrderEntity.getBranchCode())) {
            return ResponseEntity.badRequest().body(null);
        }

        if (retrieveOrderEntity.getStartDate() == null
                || retrieveOrderEntity.getEndDate() == null
                || retrieveOrderEntity.getStartDate().isAfter(retrieveOrderEntity.getEndDate())) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            List<OrderDTO> listOrderDtos = orderService
                    .retrieveOrders(retrieveOrderEntity.getBranchCode(),
                            retrieveOrderEntity.getStartDate(),
                            retrieveOrderEntity.getEndDate());

            return ResponseEntity.ok().body(listOrderDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
