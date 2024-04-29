package com.ventimetriquadriconsulting.event.controller;

import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.service.EventService;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationDTO;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationProductDTO;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "api/v1/app/event")
@AllArgsConstructor
public class EventController {

    private EventService eventService;


    @GetMapping(path = "/findeventbybranchid")
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

    @DeleteMapping(path = "/delete/workstation")
    public ResponseEntity<Void> deleteWorkstation(long workstationId){
        eventService.deleteWorkstation(workstationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @DeleteMapping(path = "/delete/event")
    public ResponseEntity<Void> deleteEvent(long eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @DeleteMapping("/{workstationId}/products/{productId}")
    public ResponseEntity<?> deleteWorkstationProduct(@PathVariable long workstationId, @PathVariable long productId) {
        return eventService.deleteProduct(workstationId, productId);
    }

    @PutMapping("/{workstationId}/products/{productId}")
    public ResponseEntity<?> updateWorkstationProduct(@PathVariable long workstationId, @PathVariable long productId, @RequestBody WorkstationProductDTO updatedProductDTO) {
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

    @PostMapping("/{eventId}/workstations/{workstationId}/products")
    public ResponseEntity<?> addProductsToWorkstation(@PathVariable long eventId,
                                                      @PathVariable long workstationId,
                                                      @RequestBody List<WorkstationProductDTO> productDTOList) {
        try {

            WorkstationDTO workstationDTO = eventService.addProductsToWorkstation(eventId, workstationId, productDTOList);
            return ResponseEntity.ok().body(workstationDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
//
//    @DeleteMapping(path = "/delete")
//    public void delete(@RequestParam("eventId") long eventId){
//        eventService.deleteEvent(eventId);
//    }
//
//    @PutMapping(path = "/close")
//    public void close(@RequestParam("eventId") long eventId){
//        eventService.closeEvent(eventId);
//    }
//
//    @PutMapping(path = "/update")
//    public void update(@RequestBody Event event){
//        eventService.update(event);
//    }
//
//    // WORKSTATION REOURCES
//    @PostMapping(path = "/workstation/create")
//    public Workstation createWorkstation(@RequestBody Workstation workstation){
//        return eventService.createWorkstation(workstation);
//    }
//
//    @PostMapping(path = "/workstation/addproduct")
//    public Workstation addProductToWorkstation(@RequestBody Workstation workstation){
//        return eventService.createWorkstation(workstation);
//    }
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

