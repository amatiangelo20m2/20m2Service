package com.ventimetriconsulting.storage.repository;

import com.ventimetriconsulting.storage.entity.Inventario;
import com.ventimetriconsulting.storage.entity.Storage;
import com.ventimetriconsulting.supplier.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductAndStorage(Product product, Storage storage);

    Optional<Inventario> findByProduct_ProductIdAndStorage_StorageId(long productId, long storageId);
}
