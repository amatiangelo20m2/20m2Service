package com.ventimetriconsulting.supplier.controller;

import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping(path = "/add")
    public ResponseEntity<SupplierDTO> addSupplier(
            @RequestBody SupplierDTO supplierDTO, @RequestParam("branchCode") String branchCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.createSupplier(supplierDTO, branchCode));
    }

    @PutMapping(path = "/editsupplier")
    public ResponseEntity<SupplierDTO> editSupplier(
            @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.editSupplier(supplierDTO));
    }

    @PostMapping(path = "/addlist")
    public ResponseEntity<List<SupplierDTO>> insertSupplierList(
            @RequestBody List<SupplierDTO> supplierDTOList,
            @RequestParam("branchCode") String branchCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.insertSupplierList(supplierDTOList, branchCode));
    }

    @PostMapping(path = "/product/add")
    public ResponseEntity<ProductDTO> insertProduct(
            @RequestBody ProductDTO productDTO, @RequestParam("supplierId") Long supplierId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.createProduct(productDTO, supplierId));
    }

    @PostMapping(path = "/product/insertlist")
    public ResponseEntity<List<ProductDTO>> insertProductList(
            @RequestBody List<ProductDTO> productDTOList,
            @RequestParam("supplierId") Long supplierId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.insertListProduct(productDTOList, supplierId));
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Boolean> removeSupplier(@RequestParam("supplierId") Long supplierId) {

        supplierService.deleteSupplier(supplierId);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PutMapping(path = "/product/update")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(supplierService.updateProduct(productDTO));
    }

    @GetMapping(path = "/retrieveSuppliers")
    public ResponseEntity<List<SupplierDTO>> retrieveAllSupplier() {
        return ResponseEntity.status(HttpStatus.OK).body(supplierService.retrieveAllSuppliers());
    }

    @PutMapping(path = "/storenewbranchexclusionlisttotupplier")
    public ResponseEntity<?> storeNewBranchExclusionListToSupplier(@RequestParam("supplierId") Long supplierId,
                                                                   @RequestBody List<String> exclusionList) {
        supplierService
                .storeNewBranchExclusionListToSupplier(supplierId, exclusionList);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/storenewbranchListNotAllowedToSeeProduct")
    public ResponseEntity<?> storeNewBranchListNotAllowedToSeeProduct(@RequestParam("supplierId") Long supplierId,
                                                                      @RequestParam("productId") Long productId,
                                                                   @RequestBody List<String> exclusionList) {

        supplierService.storeNewBranchListNotAllowedToSeeProduct(supplierId, productId, exclusionList);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
