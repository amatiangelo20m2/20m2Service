package com.ventimetriquadriconsulting.event.service;


import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.entity.dto.CateringStorageDTO;
import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.repository.CateringStorageRepository;
import com.ventimetriquadriconsulting.event.repository.EventRepository;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.workstations.entity.Workstation;
import com.ventimetriquadriconsulting.event.workstations.entity.Product;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationDTO;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import com.ventimetriquadriconsulting.event.workstations.repository.WorkstationRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private EventRepository eventRepository;
    private WorkstationRepository workstationRepository;
    private CateringStorageRepository cateringStorageRepository;

    @Transactional
    public EventDTO createEvent(EventDTO eventDto){

        Event entity = EventDTO.toEntity(eventDto);
        log.info("Store event {}", entity);

        Event save = eventRepository.save(EventDTO.toEntity(eventDto));
        return EventDTO.fromEntity(save);

    }

    public List<EventDTO> findEventByBranchCodeAndStatus(String branchCode, EventStatus eventStatus) {

        log.info("Retrieve events by branch code {} with status {}", branchCode, eventStatus);
        List<Event> byBranchCodeAndEventStatus = eventRepository
                .findByBranchCodeAndEventStatusOrderByDateEventDesc(branchCode, eventStatus);
        if(byBranchCodeAndEventStatus.isEmpty()){
            return new ArrayList<>();
        } else {
            return EventDTO.listFromEntities(new HashSet<>(byBranchCodeAndEventStatus));
        }
    }

    @Transactional
    @Modifying
    public void deleteWorkstation(long workstationId, long eventId) {
        log.info("Delete workstation by id {}", workstationId);

        Workstation workstation = workstationRepository.findById(workstationId)
                .orElseThrow(() -> new NotFoundException("Workstation not found for id " + workstationId));


        Event event = eventRepository
                .findById(eventId).orElseThrow(()
                -> new NotFoundException("Exception thowed while getting data. No event with id : "
                        + eventId + "found. Cannot delete workstation with id " + workstationId));

        event.getWorkstations().remove(workstation);
        workstationRepository.deleteById(workstationId);

    }

    @Transactional
    @Modifying
    public void deleteEvent(long eventId) {
        log.info("Delete event by id {}", eventId);
        eventRepository.deleteById(eventId);
    }


    @Transactional
    @Modifying
    public ResponseEntity<Object> deleteProduct(long workstationId, long productId) {
        log.info("Delete product with id {} from workstation with id {}", productId, workstationId);
        Optional<Workstation> workstationOptional = workstationRepository.findById(workstationId);
        if (workstationOptional.isPresent()) {
            Workstation workstation = workstationOptional.get();
            Set<Product> products = workstation.getProducts();
            products.removeIf(product -> product.getProductId() == productId);
            workstationRepository.save(workstation);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Transactional
    @Modifying
    public ResponseEntity<?> updateProduct(long workstationId, long productId, ProductDTO updatedProductDTO) {
        Optional<Event> eventOptional = eventRepository.findById(workstationId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            Set<Workstation> workstations = event.getWorkstations();
            for (Workstation workstation : workstations) {
                if (workstation.getWorkstationId() == workstationId) {
                    Set<Product> products = workstation.getProducts();
                    for (Product product : products) {
                        if (product.getProductId() == productId) {
                            // Update properties of the matched product
                            product.setProductName(updatedProductDTO.getProductName());
                            product.setQuantityInserted(updatedProductDTO.getQuantityInserted());
                            product.setQuantityConsumed(updatedProductDTO.getQuantityConsumed());
                            product.setPrice(updatedProductDTO.getPrice());
                            product.setUnitMeasure(updatedProductDTO.getUnitMeasure());
                            // Save the event to persist changes
                            Event save = eventRepository.save(event);
                            return ResponseEntity.ok().body(EventDTO.fromEntity(save));
                        }
                    }
                }
            }
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @Modifying
    public ResponseEntity<?> updateProductInsertedQuantityForWorkstation(long eventId,
                                                                         long workstationId,
                                                                         Map<Long, Double> quantityMap) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            Set<Workstation> workstations = event.getWorkstations();
            for (Workstation workstation : workstations) {
                if (workstation.getWorkstationId() == workstationId) {
                    Set<Product> products = workstation.getProducts();
                    for (Product product : products) {
                        Long productId = product.getProductId();
                        if (quantityMap.containsKey(productId)) {
                            Double newQuantityInserted = quantityMap.get(productId);
                            product.setQuantityInserted(newQuantityInserted);
                        }
                    }
                    Event save = eventRepository.save(event);
                    return ResponseEntity.ok().body(EventDTO.fromEntity(save));
                }
            }
            return ResponseEntity.notFound().build(); // Workstation not found
        } else {
            return ResponseEntity.notFound().build(); // Event not found
        }
    }

    @Transactional
    @Modifying
    public ResponseEntity<?> updateProductConsumedQuantityForWorkstation(long eventId,
                                                                         long workstationId,
                                                                         Map<Long, Double> quantityMap) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            Set<Workstation> workstations = event.getWorkstations();
            for (Workstation workstation : workstations) {
                if (workstation.getWorkstationId() == workstationId) {
                    Set<Product> products = workstation.getProducts();
                    for (Product product : products) {
                        Long productId = product.getProductId();
                        if (quantityMap.containsKey(productId)) {
                            Double newConsumedQuantity = quantityMap.get(productId);
                            product.setQuantityConsumed(newConsumedQuantity);
                        }
                    }
                    Event save = eventRepository.save(event);
                    return ResponseEntity.ok().body(EventDTO.fromEntity(save));
                }
            }
            return ResponseEntity.notFound().build(); // Workstation not found
        } else {
            return ResponseEntity.notFound().build(); // Event not found
        }
    }

    public WorkstationDTO addProductsToWorkstation(long eventId, long workstationId, List<ProductDTO> productDTOList) {
        // Retrieve the event and workstation
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        Workstation workstation = event.getWorkstations().stream()
                .filter(ws -> ws.getWorkstationId() == workstationId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Workstation not found"));

        // Convert and add product DTOs to the workstation
        List<Product> products = productDTOList.stream()
                .map(ProductDTO::toEntity)
                .toList();

        workstation.getProducts().addAll(products);

        // Save the changes
        eventRepository.save(event);

        return WorkstationDTO.fromEntity(workstation);
    }

    @Transactional
    public void closeEvent(long eventId) {
        log.info("Close event with id {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setEventStatus(EventStatus.CHIUSO);
    }


    @Transactional
    public WorkstationDTO addWorkstationToEvent(long eventId, WorkstationDTO workstationDTO) {
        log.info("Create workstation {} to event with ID {}" ,workstationDTO, eventId);

        if(workstationDTO.getWorkstationProducts() == null){
            workstationDTO.setWorkstationProducts(new HashSet<>());
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId + ". Cannot associate the workstation" ));
        Workstation workstation = WorkstationDTO.toEntity(workstationDTO);

        if(workstation.getProducts() == null) {
            workstation.setProducts(new HashSet<>());
        }

        Workstation savedWorkstation = workstationRepository.save(workstation);
        event.getWorkstations().add(savedWorkstation);

        return WorkstationDTO.fromEntity(savedWorkstation);
    }

    public CateringStorageDTO createCateringStorage(CateringStorageDTO cateringStorageDTO) {
        log.info("Create catering storage {}", cateringStorageDTO);
        return CateringStorageDTO.fromEntity(cateringStorageRepository.save(CateringStorageDTO.toEntity(cateringStorageDTO)));
    }
}
