package com.ventimetriconsulting.branch.repository;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByBranchCode(String branchCode);

    List<Branch> findByType(BranchType type);
}
