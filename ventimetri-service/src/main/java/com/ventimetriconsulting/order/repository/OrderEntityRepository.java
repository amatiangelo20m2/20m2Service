package com.ventimetriconsulting.order.repository;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.order.entIty.Order;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.OrderTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderEntityRepository extends JpaRepository<Order, Long> {

    List<Order> findByBranchBranchCodeAndIncomingDateBetweenAndOrderStatusNot(String branchCode, LocalDate startDate, LocalDate endDate, OrderStatus orderStatus);
    List<Order> findByBranchBranchCodeAndIncomingDateBetweenAndOrderStatus(String branchCode, LocalDate startDate, LocalDate endDate, OrderStatus orderStatus);
    List<Order> findByCodeTargetAndIncomingDateBetween(String branchCodeTarget, LocalDate startDate, LocalDate endDate);
    List<Order> findByOrderTargetAndIncomingDateAndOrderStatus(OrderTarget orderTarget, LocalDate date, OrderStatus orderStatus);
    @Query("SELECT orders FROM Order_Entity orders WHERE orders.branch.branchCode = ?1 OR orders.codeTarget =?1 AND orders.orderStatus <> ?2")
    List<Order> findByBranchBranchCodeAndOrderStatusNot(String branchCode, OrderStatus orderStatus);

}
