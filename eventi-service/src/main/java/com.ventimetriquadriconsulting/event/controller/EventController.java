package com.ventimetriquadriconsulting.event.controller;

import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.entity.dto.CateringStorageDTO;
import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.entity.dto.ExpenseEventDTO;
import com.ventimetriquadriconsulting.event.service.CateringStorageService;
import com.ventimetriquadriconsulting.event.service.EventService;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationDTO;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "api/v1/app/event")
@AllArgsConstructor
@Slf4j
public class EventController {

    private EventService eventService;
    private CateringStorageService cateringStorageService;


    @GetMapping(path = "/findeventbybranchcode")
    public ResponseEntity<List<EventDTO>> retrieveEventsByBranchCode(@RequestParam("branchCode") String branchCode,
                                                                     @RequestParam EventStatus eventStatus){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.findEventByBranchCodeAndStatus(branchCode, eventStatus));
    }

    @PostMapping(path = "/save")
    public ResponseEntity<EventDTO> save(@RequestBody EventDTO event) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.createEvent(event));
    }

    @PutMapping(path = "/{eventId}/createworkstation")
    public ResponseEntity<WorkstationDTO> createWorkstation(@PathVariable("eventId") long eventId,
                                                            @RequestBody WorkstationDTO workstationDTO){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.addWorkstationToEvent(
                        eventId,
                        workstationDTO));
    }

    @DeleteMapping(path = "/delete/workstation")
    public ResponseEntity<Void> deleteWorkstation(long workstationId, long eventId){
        eventService.deleteWorkstation(workstationId, eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @DeleteMapping("/{workstationId}/products/{productId}")
    public ResponseEntity<?> deleteWorkstationProduct(@PathVariable long workstationId, @PathVariable long productId) {
        return eventService.deleteProduct(workstationId, productId);
    }

    @PutMapping("/{cateringStorageId}/loadprod/storagecatering")
    public ResponseEntity<CateringStorage> loadProductsIntoStorageCatering(@PathVariable long cateringStorageId,
                                                             @RequestBody Map<Long, Double> quantityMap) {
        return ResponseEntity.ok().body(eventService.loadProductIntoStorageVan(cateringStorageId, quantityMap));
    }

    @PutMapping("/{workstationId}/products/{productId}")
    public ResponseEntity<?> updateWorkstationProduct(@PathVariable long workstationId, @PathVariable long productId, @RequestBody ProductDTO updatedProductDTO) {
        ResponseEntity<?> responseEntity = eventService.updateProduct(workstationId, productId, updatedProductDTO);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @PutMapping("/{eventId}/workstations/{workstationId}/products/insertedquantity")
    public ResponseEntity<?> updateProductInsertedQuantityForWorkstation(@PathVariable long eventId, @PathVariable long workstationId, @RequestBody Map<Long, Double> quantityMap) {
        ResponseEntity<?> responseEntity = eventService.updateProductInsertedQuantityForWorkstation(eventId, workstationId, quantityMap);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @PutMapping("/{eventId}/workstations/{workstationId}/products/consumedquantity")
    public ResponseEntity<?> updateProductConsumedQuantityForWorkstation(@PathVariable long eventId, @PathVariable long workstationId, @RequestBody Map<Long, Double> quantityMap) {
        ResponseEntity<?> responseEntity = eventService.updateProductConsumedQuantityForWorkstation(eventId, workstationId, quantityMap);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @DeleteMapping("/{eventId}/delete")
    public ResponseEntity<?> deleteEvent(@PathVariable long eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/{eventId}/close")
    public ResponseEntity<?> closeEvent(@PathVariable long eventId){
        eventService.closeEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/createcateringstorage")
    public ResponseEntity<CateringStorageDTO> createCateringStorage(@RequestBody CateringStorageDTO cateringStorageDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService
                        .createCateringStorage(cateringStorageDTO));
    }

    @GetMapping("/{branchCode}/retrievecateringstorage")
    public ResponseEntity<List<CateringStorageDTO>> retrieveCateringStorageEvent(@PathVariable String branchCode) {

        log.info("Retrieve Storage for events by branch code {}", branchCode);
        Optional<List<CateringStorage>> byBranchCode = cateringStorageService.findByBranchCode(branchCode);

        return byBranchCode.map(cateringStorages
                        -> ResponseEntity.status(HttpStatus.OK).body(CateringStorageDTO.fromEntityList(cateringStorages)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK).body(null));
    }

    @PutMapping("/{cateringStorageId}/addproduct/")
    public ResponseEntity<List<ProductDTO>> addProductToStorageCatering(@PathVariable long cateringStorageId,
                                                                        @RequestBody List<ProductDTO> productDTOList){

        List<ProductDTO> productDTOS = cateringStorageService.addProducts(cateringStorageId, productDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(productDTOS);
    }

    @DeleteMapping("/{cateringStorageId}/removeproduct/{productId}")
    public ResponseEntity<?> removeProductFromStorageMobile(@PathVariable long cateringStorageId,
                                                                        @PathVariable long productId){

        cateringStorageService.removeProducts(cateringStorageId, productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}/deleteproductfromcateringstorage/{cateringStorageId}")
    public ResponseEntity<?> deleteProductFromCateringStorage(@PathVariable long productId, @PathVariable long cateringStorageId){
        cateringStorageService.deleteProduct(productId, cateringStorageId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{eventId}/expence/save")
    public ResponseEntity<ExpenseEventDTO> saveExpence(@PathVariable long eventId, @RequestBody ExpenseEventDTO expenseEventDTO){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.saveExpence(eventId, expenseEventDTO));
    }

    @DeleteMapping("/{eventId}/expence/delete")
    public ResponseEntity<?> deleteExpence(@PathVariable long eventId, @RequestBody ExpenseEventDTO expenseEventDTO){
        eventService.deleteExpence(eventId, expenseEventDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{eventId}/expence/update")
    public ResponseEntity<ExpenseEventDTO> updateExpence(@PathVariable long eventId,
                                                         @RequestBody ExpenseEventDTO expenseEventDTO){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateExpence(eventId, expenseEventDTO));
    }

    @GetMapping("/{eventId}/retrieveworkstation/{workstationId}")
    public ResponseEntity<WorkstationDTO> retrieveWorkstation(@PathVariable long eventId,
                                                         @PathVariable long workstationId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.retrieveWorkstationById(eventId, workstationId));
    }

    @PostMapping(path = "/{eventiId}/{cateringStorageId}/workstation/{workstationId}/addproducts")
    public ResponseEntity<List<ProductDTO>> addProductToWorkstation(@PathVariable long eventiId,
                                                                    @PathVariable long workstationId,
                                                                    @PathVariable long cateringStorageId,
                                                                    @RequestParam List<Long> productIds) {

        List<ProductDTO> productDTOS = eventService.addProductsToWorkstation(eventiId, workstationId, productIds, cateringStorageId);

        return ResponseEntity.status(HttpStatus.OK).body(productDTOS);
    }

    @DeleteMapping(path = "/deletefromworkstation/{workstationId}/product/{productId}")
    public ResponseEntity<?> deleteProductFromWorkstation(@PathVariable long workstationId, @PathVariable long productId){
        eventService.deleteProductFromWorkstation(workstationId, productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PutMapping(path = "/loadamountforworkstationproduct/{workstationId}")
    public ResponseEntity<?> loadAmountForWorkstationProduct(@PathVariable long workstationId,
                                                               @RequestBody Map<Long, Double> insertValueMapProductIdAmountToInsert) {

        eventService.setLoadQuantity(workstationId, insertValueMapProductIdAmountToInsert);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/unloadamountforworkstationproduct/{workstationId}")
    public ResponseEntity<?> unloadAmountForWorkstationProduct(@PathVariable long workstationId,
                                                               @RequestBody Map<Long, Double> insertValueMapProductIdAmountToInsert) {

        eventService.setUnLoadQuantity(workstationId, insertValueMapProductIdAmountToInsert);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//
//    // EXPENCE RESOURCES
//    @GetMapping(path = "/expence/retrievebyeventid")
//    public List<ExpenceEvent> retrieveAllExpenpencesByEventId(@RequestParam("eventid") long eventId){
//        return eventService.findExpencesByEventId(eventId);
//    }
//
//    @PostMapping(path = "/expence/create")
//    public ExpenceEvent saveExpence(@RequestBody ExpenceEvent expenceEvent){
//        return eventService.saveExpence(expenceEvent);
//    }
//    @DeleteMapping(path = "expence/delete")
//    public void deleteExpence(@RequestBody ExpenceEvent expenceEvent){
//        eventService.deleteEvent(expenceEvent);
//    }
//
//    @PutMapping(path = "expence/update")
//    public void updateExpence(@RequestBody ExpenceEvent expenceEvent){
//        eventService.updateExpence(expenceEvent);
//    }
}

