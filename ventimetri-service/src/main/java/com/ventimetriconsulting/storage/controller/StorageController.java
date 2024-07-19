package com.ventimetriconsulting.storage.controller;

import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.storage.entity.dto.InventarioDTO;
import com.ventimetriconsulting.storage.entity.dto.StorageDTO;
import com.ventimetriconsulting.storage.entity.dto.TransactionInventoryRequest;
import com.ventimetriconsulting.storage.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/storage/")
@AllArgsConstructor
public class StorageController {

    private StorageService storageService;
    private BranchService branchService;

    @GetMapping(path = "/retrieve/bybranchcode")
    public ResponseEntity<List<StorageDTO>> retrieveStoragesByBranchCode(@RequestParam("branchCode") String branchCode){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branchService.retrieveStoragesByBranchCode(branchCode));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<StorageDTO> addStorage(
            @RequestBody StorageDTO storageDTO,
            @RequestParam("branchCode") String branchCode) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.createStorage(storageDTO, branchCode));
    }

    @PostMapping(path = "/addproduct")
    public ResponseEntity<InventarioDTO> addProduct(
            @RequestParam("productId") long productId,
            @RequestParam("storageId") long storageId,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertProductToStorage(
                        productId,
                        storageId,
                        userName,
                        0));
    }

    /**
     *
     * This method retrieve the supplier and all the associated products
     * and put in a storage. It give back the inventory list
     *
     * @param supplierId
     * @param storageId
     * @param userName
     * @return
     */
    @PostMapping(path = "/insertsupplierproducts")
    public ResponseEntity<List<InventarioDTO>> insertProductsFromSupplierList(
            @RequestParam("supplierId") long supplierId,
            @RequestParam("storageId") long storageId,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertSupplierProductsintoStorage(
                        supplierId,
                        storageId,
                        userName));
    }

    @PutMapping(path = "/putdata")
    public ResponseEntity<InventarioDTO> putData(
            @RequestParam("inventarioId") long inventarioId,
            @RequestParam("insertedAmount") long insertedAmount,
            @RequestParam("deletedAmount") long deletedAmount,
            @RequestParam("userName") String userName) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.putDataIntoInventario(
                        inventarioId,
                        insertedAmount,
                        deletedAmount,
                        userName));
    }

    @DeleteMapping(path = "/delete/product")
    public ResponseEntity<?> removeProductFromStorage(
            @RequestParam("inventarioId") long inventarioId){

        storageService.deleteProductFromStorage(inventarioId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @PutMapping(path = "/insert/inventariodata")
    public ResponseEntity<StorageDTO> insertDataIntoInventario(
            @RequestBody TransactionInventoryRequest transactionInventoryRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(storageService.insertDataIntoInventario(transactionInventoryRequest));
    }

    @PutMapping(path = "/update/stock/{storageId}/{userName}/{branchCode}")
    public ResponseEntity<StorageDTO> updateStock(@PathVariable long storageId,
                                                  @RequestBody Map<Long, Double> stockValues,
                                                  @PathVariable String userName,
                                                  @PathVariable String branchCode){
        StorageDTO storageDTO = storageService.updateStockValueInventario(storageId, stockValues, userName, branchCode);
        return ResponseEntity.status(HttpStatus.OK).body(storageDTO);
    }
}
