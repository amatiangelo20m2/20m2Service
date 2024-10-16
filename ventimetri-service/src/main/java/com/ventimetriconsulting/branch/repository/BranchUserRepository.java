package com.ventimetriconsulting.branch.repository;

import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchUserRepository  extends JpaRepository<BranchUser, Long> {

    @Query("SELECT bu FROM BranchUser bu WHERE bu.userCode = ?1")
    Optional<List<BranchUser>> findBranchesByUserCode(String userCode);

    @Query("SELECT bu FROM BranchUser bu WHERE bu.userCode = ?1 AND bu.branch.branchCode = ?2")
    Optional<BranchUser> findBranchesByUserCodeAndBranchCode(String userCode, String branchCode);

    @Query("SELECT bu FROM BranchUser bu WHERE " +
            "bu.branch.branchCode = :branchCode " +
            "AND bu.fMCToken IS NOT NULL AND " +
            "bu.userCode != :userCodeSender")
    List<BranchUser> findFMCTokensByBranchCode(String branchCode, String userCodeSender);


    @Query("SELECT bu.fMCToken FROM BranchUser bu WHERE " +
            "bu.branch.branchCode = :branchCode " +
            "AND bu.fMCToken IS NOT NULL AND bu.role = :role")
    List<String> findFMCTokensByBranchCodeAndRole(String branchCode, Role role);

    @Query("SELECT bu FROM BranchUser bu WHERE bu.branch.branchCode = ?1")
    Optional<List<BranchUser>> findBranchesByBranchCode(String branchCode);

    @Query("SELECT bu FROM BranchUser bu WHERE bu.userCode = ?1 AND bu.branch.branchCode = ?2")
    Optional<BranchUser> findByUserCodeAndBranchCode(String userCode, String branchCode);


    @Query("SELECT bu FROM BranchUser bu WHERE " +
            "bu.branch.branchCode = :branchCode " +
            "AND bu.fMCToken IS NOT NULL AND bu.role = :role")
    Optional<BranchUser> findByBranchCodeAndRole(String branchCode, Role role);

    @Query("SELECT bu FROM BranchUser bu WHERE " +
            "bu.branch.branchCode = :branchCode " +
            "AND bu.fMCToken IS NOT NULL AND bu.userCode = :userCode AND bu.role = :role")
    Optional<BranchUser> findByBranchCodeAndUserCode(String branchCode, String userCode, Role role);


    @Transactional
    @Modifying
    @Query("DELETE FROM BranchUser b WHERE b.branch.branchId = :branchId")
    void deleteByBranchId(long branchId);
}
