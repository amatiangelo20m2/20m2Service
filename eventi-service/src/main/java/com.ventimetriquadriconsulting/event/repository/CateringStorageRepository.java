package com.ventimetriquadriconsulting.event.repository;

import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CateringStorageRepository extends JpaRepository<CateringStorage, Long> {
    Optional<List<CateringStorage>> findByBranchCode(String branchCode);
}
