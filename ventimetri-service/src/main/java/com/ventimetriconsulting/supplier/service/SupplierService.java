package com.ventimetriconsulting.supplier.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.CreateProductException;
import com.ventimetriconsulting.branch.exception.customexceptions.ProductNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.SupplierNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO, String branchCode) {

        log.info("Crete supplier {}. Associate it with branch with code {}", supplierDTO, branchCode);

        Supplier supplier = SupplierDTO.fromDTO(supplierDTO);

        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with code: " + branchCode + ". Cannot associate the supplier" ));

        Supplier save = supplierRepository.save(supplier);
        branch.getSuppliers().add(save);

        return SupplierDTO.fromEntity(save);
    }

    @Transactional
    @Modifying
    public ProductDTO createProduct(ProductDTO productDTO,
                                    Long supplierId) {
        try{
            log.info("Saving product {} for supplier with id {}", productDTO, supplierId);

            Supplier supplier = supplierRepository
                    .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));


            Product product = ProductDTO.fromDTO(productDTO);
            product.setSupplier(supplier);
            supplier.getProducts().add(product);

            Product savedProduct = productRepository.save(product);

            ProductDTO dto = ProductDTO.toDTO(savedProduct);
            log.info("Saved prod (dto): " + dto);
            return dto;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new CreateProductException(e.getMessage());
        }

    }

    @Transactional
    @Modifying
    public void unlinkSupplierFromBranch(Long supplierId, Long branchId) {
        log.info("Remove supplier with id {} from branch with id {}",
                supplierId,
                branchId);

        Branch branch = branchRepository
                .findById(branchId).orElseThrow(() -> new BranchNotFoundException("Branch not found with code: "
                        + branchId + ". Cannot unlink supplier with code " + supplierId));

        boolean removed = branch.getSuppliers().removeIf(supplier -> supplier.getSupplierId() == supplierId);
        if (removed) {
            branchRepository.save(branch);
        } else {
            throw new RuntimeException("Supplier not found in branch");
        }
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {

        log.info("Updating product {}", productDTO);

        Product product = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("No product found with code " + productDTO.getProductId()));

        product.setProductCode(productDTO.getProductCode());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());

        product.setVatPrice(productDTO.getVatPrice());
        product.setCategory(productDTO.getCategory());
        product.setDescription(productDTO.getDescription());

        product.setUnitMeasure(productDTO.getUnitMeasure());
        product.setSku(productDTO.getSku());
        product.setVatApplied(productDTO.getVatApplied());
        product.setAvailable(productDTO.isAvailable());
        product.setDeleted(productDTO.isDeleted());

        return ProductDTO.toDTO(product);
    }

    @Transactional
    @Modifying
    public List<ProductDTO> insertListProduct(List<ProductDTO> productDTOList,
                                              Long supplierId) {

        log.info("Saving product list {} for supplier with id {}", productDTOList, supplierId);
        Supplier supplier = supplierRepository
                .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));

        Map<String, Product> existingProductsMap = supplier.getProducts().stream()
                .collect(Collectors.toMap(
                        product -> (product.getName().toLowerCase() + "-" + product.getUnitMeasure().toString()),
                        product -> product
                ));

        for (ProductDTO productDTO : productDTOList) {
            String productKey = productDTO.getName().toLowerCase() + "-" + productDTO.getUnitMeasure().toString();

            if (existingProductsMap.containsKey(productKey)) {
                Product product = existingProductsMap.get(productKey);
                log.info("Updating product {}", product);
                product.setName(productDTO.getName());
                product.setPrice(productDTO.getPrice());
                product.setVatPrice(productDTO.getVatPrice());
                product.setCategory(productDTO.getCategory());
                product.setDescription(productDTO.getDescription());
                product.setUnitMeasure(productDTO.getUnitMeasure());
                product.setSku(productDTO.getSku());
                product.setVatApplied(productDTO.getVatApplied());
                product.setAvailable(productDTO.isAvailable());
                product.setDeleted(productDTO.isDeleted());

            } else {
                log.info("Save brand new prod: {}", productDTO);
                createProduct(productDTO, supplierId);
            }
        }

        supplier = supplierRepository
                .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));

        return ProductDTO.toDTOList(supplier.getProducts(), true, false);
    }

    @Transactional
    public List<SupplierDTO> insertSupplierList(List<SupplierDTO> supplierDTOList,
                                                String branchCode) {
        List<SupplierDTO> savedSuppliersDTO = new ArrayList<>();

        for (SupplierDTO supplierDTO : supplierDTOList){
            savedSuppliersDTO.add(createSupplier(supplierDTO, branchCode));
        }

        return savedSuppliersDTO;
    }

    public List<SupplierDTO> retrieveAllSuppliers() {

        log.info("Retrieve all suppliers..");
        List<Supplier> allSuppliers = supplierRepository.findAll();


        return SupplierDTO.toDTOList(new HashSet<>(allSuppliers));
    }

    @Transactional
    @Modifying
    public boolean deleteSupplier(Long supplierId) {
        log.info("Delete supplier with id {}", supplierId);

        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);
        if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            // Deleting all products related to this supplier
            supplier.getProducts().forEach(product -> productRepository.deleteById(product.getProductId()));
            // Deleting the supplier
            supplierRepository.delete(supplier);
            return true;
        } else {
            throw new RuntimeException("Supplier not found");
        }
    }

}
