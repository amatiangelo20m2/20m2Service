package com.ventimetriconsulting.supplier.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
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
    @Modifying
    public void deleteProductById(Long productId,
                                  Long supplierId) {
        log.info("Remove product by id {} for supplier with id {}", productId, supplierId);

        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);
        if (supplierOptional.isPresent()) {
            Supplier supplier = supplierOptional.get();
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                supplier.removeProduct(product);
                productRepository.delete(product); // Delete product from the database
                supplierRepository.save(supplier); // Save the updated supplier
            } else {
                throw new RuntimeException("Product not found");
            }
        } else {
            throw new RuntimeException("Supplier not found");
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

        return ProductDTO.toDTO(product);
    }

    @Transactional
    @Modifying
    public List<ProductDTO> insertListProduct(List<ProductDTO> productDTOList,
                                        Long supplierId) {

        log.info("Saving product list {} for supplier with id {}", productDTOList, supplierId);

        Supplier supplier = supplierRepository
                .findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found with code: " + supplierId + ". Cannot create any product"));

        List<ProductDTO> productDTOS = new ArrayList<>();

        List<String> productNames = supplier.getProducts().stream()
                .map(Product::getName)
                .toList();

        for(ProductDTO productDTO : productDTOList) {

            if(productNames.contains(productDTO.getName())){
                for(Product product : supplier.getProducts()){
                    if(Objects.equals(productDTO.getName().toLowerCase(), product.getName().toLowerCase())){

                        log.info("Updating product {}", product);
                        product.setName(productDTO.getName());
                        product.setPrice(productDTO.getPrice());
                        product.setVatPrice(productDTO.getVatPrice());
                        product.setCategory(productDTO.getCategory());
                        product.setDescription(productDTO.getDescription());

                        product.setUnitMeasure(productDTO.getUnitMeasure());
                        product.setSku(productDTO.getSku());
                        product.setVatApplied(productDTO.getVatApplied());

                        productDTOS.add(ProductDTO.toDTO(product));
                    }
                }

            }else{
                ProductDTO product = createProduct(productDTO, supplierId);
                log.info("Stored product: " + product);
                productDTOS.add(product);
            }
        }

        log.info("List output : " + productDTOS);
        return productDTOS;
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
