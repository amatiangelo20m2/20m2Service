package com.ventimetriquadriconsulting.event.service;


import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.entity.ExpenseEvent;
import com.ventimetriquadriconsulting.event.entity.dto.CateringStorageDTO;
import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.entity.dto.ExpenseEventDTO;
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

import java.time.LocalDate;
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

    public List<ProductDTO> addProductsToWorkstation(
            long eventId,
            long workstationId,
            List<Long> productIds,
            long cateringStorageId) {

        // Retrieve the event and workstation

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));


        Workstation workstation = event.getWorkstations().stream()
                .filter(ws -> ws.getWorkstationId() == workstationId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Workstation not found"));

        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageId).orElseThrow(()
                -> new NotFoundException("Catering storage not found for id [" + cateringStorageId + "]"));

        List<Product> productsToAdd = cateringStorage.getCateringStorageProducts().stream()
                .filter(product -> productIds.contains(product.getProductId()))
                .toList();

        if(productsToAdd.isEmpty()) {
            log.warn("No products found for the given product IDs: {}", productIds);
        }

        workstation.getProducts().addAll(productsToAdd);

        // Save the changes
        eventRepository.save(event);

        return ProductDTO.listFromEntities(productsToAdd);
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

    @Transactional
    public ExpenseEventDTO saveExpence(long eventId, ExpenseEventDTO expenseEventDTO) {
        log.info("Create expence {} for event with id {}", expenseEventDTO, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event not found for id " + eventId));

        ExpenseEvent expenseEvent = expenseEventDTO.toEntity();

        expenseEvent.setExpenseId(UUID.randomUUID().toString());
        expenseEvent.setDateInsert(LocalDate.now());
        event.getExpenseEvents().add(expenseEvent);

        eventRepository.save(event);

        return ExpenseEventDTO.fromEntity(expenseEvent);
    }

    @Transactional
    @Modifying
    public void deleteExpence(long eventId, ExpenseEventDTO expenseEventDTO) {
        log.info("Delete expence {} for event with id {}", expenseEventDTO, eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event not found for id " + eventId));

        for (ExpenseEvent expenseEvent : event.getExpenseEvents()) {
            if (Objects.equals(expenseEvent.getExpenseId(), expenseEventDTO.getExpenseId())) {
                event.getExpenseEvents().remove(expenseEvent);
                break;
            }
        }
        eventRepository.save(event);
    }

    @Transactional
    @Modifying
    public ExpenseEventDTO updateExpence(long eventId, ExpenseEventDTO updatedExpenseEvent) {
        log.info("Update expence {} for event with id {}", updatedExpenseEvent, eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event not found for id " + eventId));

        ExpenseEvent updatedExpense = null;

        for (ExpenseEvent expenseEvent : event.getExpenseEvents()) {
            if (Objects.equals(expenseEvent.getExpenseId(), updatedExpenseEvent.getExpenseId())) {
                // Update the attributes of the found ExpenseEvent
                expenseEvent.setDescription(updatedExpenseEvent.getDescription());
                expenseEvent.setPrice(updatedExpenseEvent.getPrice());
                expenseEvent.setAmount(updatedExpenseEvent.getAmount());
                expenseEvent.setDateInsert(updatedExpenseEvent.getDateInsert());
                updatedExpense = expenseEvent;
            }
        }
        eventRepository.save(event);

        return ExpenseEventDTO.fromEntity(Objects.requireNonNull(updatedExpense));
    }

    @Transactional
    @Modifying
    public void deleteProductFromWorkstation(long workstationId, long productId) {
        log.info("Delete product form workstation with id {} - Product to remove has id {}", workstationId, productId);

        Workstation workstation = workstationRepository.findById(workstationId).orElseThrow(() -> new NotFoundException("Workstation not found for id " + workstationId));
        Optional<Product> productOptional = workstation.getProducts().stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst();
        productOptional.ifPresent(product -> workstation.getProducts().remove(product));
    }

    @Transactional
    @Modifying
    public void setLoadQuantity(long workstationId, Map<Long, Double> insertValueMapProductIdAmountToInsert) {
        log.info("This map contain the product id and the amount to add in order to perform a load product into workstation event. MAP: {} and it will be apply to a workstation with id {}", insertValueMapProductIdAmountToInsert, workstationId);

        Workstation workstation = workstationRepository.findById(workstationId).orElseThrow(()
                -> new NotFoundException("Workstation not found for id " + workstationId));

        for (Product product : workstation.getProducts()) {
            long productId = product.getProductId();
            if (insertValueMapProductIdAmountToInsert.containsKey(productId)) {
                double quantityInserted = insertValueMapProductIdAmountToInsert.get(productId);
                product.setQuantityInserted(quantityInserted);
            }
        }
    }

    @Transactional
    @Modifying
    public void setUnLoadQuantity(long workstationId, Map<Long, Double> insertValueMapProductIdAmountToInsert) {
        log.info("This map contain the product id and the amount to add in order to perform a unload product into workstation event. MAP: {} and it will be apply to a workstation with id {}", insertValueMapProductIdAmountToInsert, workstationId);

        Workstation workstation = workstationRepository.findById(workstationId).orElseThrow(()
                -> new NotFoundException("Workstation not found for id " + workstationId));

        for (Product product : workstation.getProducts()) {
            long productId = product.getProductId();
            if (insertValueMapProductIdAmountToInsert.containsKey(productId)) {
                double quantityInserted = insertValueMapProductIdAmountToInsert.get(productId);
                product.setQuantityConsumed(quantityInserted);
            }
        }
    }
}
