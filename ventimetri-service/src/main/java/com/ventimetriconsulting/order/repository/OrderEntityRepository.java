package com.ventimetriconsulting.order.repository;

import com.ventimetriconsulting.order.entIty.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderEntityRepository extends JpaRepository<Order, Long> {

    List<Order> findByBranchBranchCodeAndInsertedDateBetween(String branchCode, LocalDate startDate, LocalDate endDate);

//    @Query("SELECT o FROM Order_Entity o WHERE o.branch = ?1 AND o.incomingDate = ?2")
//    List<Order> findOrdersByIncomingDate(Branch branch, LocalDate incomingDate);

}
