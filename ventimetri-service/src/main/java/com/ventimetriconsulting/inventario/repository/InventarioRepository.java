package com.ventimetriconsulting.inventario.repository;

import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.entity.Storage;
import com.ventimetriconsulting.supplier.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductAndStorage(Product product, Storage storage);
}
