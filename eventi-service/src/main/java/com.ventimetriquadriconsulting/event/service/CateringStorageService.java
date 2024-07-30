package com.ventimetriquadriconsulting.event.service;

import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.repository.CateringStorageRepository;
import com.ventimetriquadriconsulting.event.workstations.entity.Product;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CateringStorageService {

    private CateringStorageRepository cateringStorageRepository;


    public Optional<List<CateringStorage>> findByBranchCode(String branchCode) {
        return cateringStorageRepository.findByBranchCode(branchCode);
    }

    @Transactional
    public List<ProductDTO> addProductsToVanStorage(long cateringStorageId,
                                                    List<ProductDTO> productDTOList) {

        log.info("Adding products to storage van with id {} - Product List: {}", cateringStorageId, productDTOList);

        Optional<CateringStorage> cateringStorageOptional = cateringStorageRepository.findById(cateringStorageId);

        if (cateringStorageOptional.isPresent()) {
            CateringStorage cateringStorage = cateringStorageOptional.get();

            Set<Product> existingProducts = cateringStorage.getCateringStorageProducts();

            // Convert the list of product DTOs to entities
            List<Product> newProducts = productDTOList.stream()
                    .map(ProductDTO::toEntity)
                    .toList();

            // Add the new products to the existing products
            existingProducts.addAll(newProducts);

            // Update the catering storage entity with the new products
            cateringStorage.setCateringStorageProducts(existingProducts);

            // Save the updated catering storage entity
            CateringStorage savedCateringStorage = cateringStorageRepository.save(cateringStorage);

            return savedCateringStorage.getCateringStorageProducts().stream()
                    .filter(newProducts::contains)
                    .map(ProductDTO::fromEntity)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Modifying
    @Transactional
    public void deleteProduct(long productId, long cateringStorageId) {
        log.info("Deleting product with ID {} from CateringStorage with ID {}", productId, cateringStorageId);

        // Find the CateringStorage entity by its ID
        Optional<CateringStorage> cateringStorageOptional = cateringStorageRepository.findById(cateringStorageId);

        // Check if the CateringStorage entity exists
        cateringStorageOptional.ifPresent(cateringStorage -> {
            // Get the set of products from the CateringStorage
            Set<Product> cateringStorageProducts = cateringStorage.getCateringStorageProducts();

            // Remove the product with the specified productId
            cateringStorageProducts.removeIf(product -> product.getProductId() == productId);

            // Update the CateringStorage entity with the modified product set
            cateringStorage.setCateringStorageProducts(cateringStorageProducts);

            // Save the updated CateringStorage entity
            cateringStorageRepository.save(cateringStorage);
        });
    }

    @Transactional
    @Modifying
    public void removeProductsFromStorage(long cateringStorageId, long productId) {
        log.info("Remove product with id {} from storage mobile with id {}", productId, cateringStorageId);
        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageId).orElseThrow(()
                -> new NotFoundException("Exception thrown while getting data for catering storage with id : " + cateringStorageId + ". Cannot retrieve catering storage and delete product with id " + productId));

        cateringStorage.getCateringStorageProducts().removeIf(product -> product.getProductId() == productId);

        cateringStorageRepository.save(cateringStorage);
    }
}
