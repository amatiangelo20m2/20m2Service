package com.ventimetriconsulting.storage.repository;

import com.ventimetriconsulting.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
}
