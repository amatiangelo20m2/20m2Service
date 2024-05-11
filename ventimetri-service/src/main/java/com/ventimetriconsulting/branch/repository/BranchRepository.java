package com.ventimetriconsulting.branch.repository;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.inventario.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByBranchCode(String branchCode);

    @Query("SELECT b.storages FROM Branch b WHERE b.branchCode = :branchCode")
    Optional<List<Storage>> findStoragesByBranchCode(String branchCode);

    List<Branch> findByType(BranchType type);

    @Query("SELECT b.name FROM Branch b WHERE b.branchCode = :branchCode")
    Optional<String> findBranchNameByBranchCode(String branchCode);

    @Transactional
    @Modifying
    @Query("DELETE FROM Branch b WHERE b.branchCode = :branchCode")
    void deleteByBranchCode(String branchCode);
}
