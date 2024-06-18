package com.ventimetriconsulting.supplier.repository;

import com.ventimetriconsulting.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Supplier s WHERE s.supplierId = :supplierId")
    void deleteBySupplierId(Long supplierId);

    @Query("SELECT supplier.name FROM Supplier supplier WHERE supplier.supplierCode = :supplierCodeTarget")
    Optional<String> findSupplierNameByCode(String supplierCodeTarget);


    @Modifying
    @Transactional
    @Query("UPDATE Supplier s SET s.isHideFromOrderList = CASE WHEN s.isHideFromOrderList = true THEN false ELSE true END WHERE s.supplierId = :supplierId")
    void toggleIsHideFromOrderList(@Param("supplierId") long supplierId);


}
