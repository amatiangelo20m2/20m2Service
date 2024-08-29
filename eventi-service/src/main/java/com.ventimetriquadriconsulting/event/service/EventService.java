package com.ventimetriquadriconsulting.event.service;


import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.entity.ExpenseEvent;
import com.ventimetriquadriconsulting.event.entity.dto.CateringStorageDTO;
import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.entity.dto.ExpenseEventDTO;
import com.ventimetriquadriconsulting.event.notification.entity.NotificationEntity;
import com.ventimetriquadriconsulting.event.notification.entity.RedirectPage;
import com.ventimetriquadriconsulting.event.notification.service.MessageSender;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private EventRepository eventRepository;
    private WorkstationRepository workstationRepository;
    private CateringStorageRepository cateringStorageRepository;
    private CateringStorageService cateringStorageService;

    private MessageSender messageSender;

    private WebClient.Builder loadBalancedWebClientBuilder;

    @Transactional
    public EventDTO createEvent(EventDTO eventDto){

        Event entity = EventDTO.toEntity(eventDto);
        log.info("Create event {}", entity);
        Event save = eventRepository.save(EventDTO.toEntity(eventDto));

//        sendNotification(eventDto);

//        messageSender.enqueMessage(NotificationEntity
//                .builder()
//                        .title("" + eventDto.getCreatedBy() + " ha creato un evento")
//                        .message("")
//                        .redirectPage(RedirectPage.CATERING)
//                        .
//                .build());

        return EventDTO.fromEntity(save);

    }

//    private void sendNotification(EventDTO eventDto) {
//        try{
//            UserResponseEntity userResponseEntity = loadBalancedWebClientBuilder.build()
//                    .get()
//                    .uri("http://auth-service/ventimetriauth/api/auth/retrievebyusercode",
//                            uriBuilder -> uriBuilder.queryParam("userCode", branchUser.getUserCode())
//                                    .build())
//                    .retrieve()
//                    .bodyToMono(UserResponseEntity.class)
//                    .block();
//        }catch (Exception e){
//            log.error("Error - problems during sending notification while the following exception happen [{}]", e.toString());
//        }
//    }

    public List<EventDTO> findEventByBranchCodeAndStatus(String branchCode, EventStatus eventStatus, String startDate, String endDate) {

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
                        -> new NotFoundException("Exception throwed while getting data. No event with id : "
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

        log.info("Add products with ids {} to the workstation with id {}", productIds, workstationId);
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
            return new ArrayList<>();
        }else{
            List<Product> list = productsToAdd.stream()
                    .map(Product::new).toList();
            workstation.getProducts().addAll(list);
            eventRepository.save(event);

            return ProductDTO.listFromEntities(list);
        }


    }

    @Transactional
    @Modifying
    public void closeEvent(long eventId) {

        log.info("Close event with id {}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found with id " + eventId));
        CateringStorage cateringStorage = cateringStorageRepository.findById(event.getCateringStorageId()).orElseThrow(() -> new NotFoundException("Storage not found for id " + event.getCateringStorageId()));

        Map<Long, Double> productDifferenceMap = new HashMap<>();

        for (Workstation workstation : event.getWorkstations()) {
            for (Product product : workstation.getProducts()) {
                long productId = product.getProductId();
                double difference = product.getQuantityInserted() - product.getQuantityConsumed();

                productDifferenceMap.put(productId, productDifferenceMap.getOrDefault(productId, 0.0) + difference);
            }
        }

        for (Product product : cateringStorage.getCateringStorageProducts()) {
            long productId = product.getProductId();
            if (productDifferenceMap.containsKey(productId)) {
                double consumedQuantity = productDifferenceMap.get(productId);
                product.setQuantityInserted(product.getQuantityInserted() - consumedQuantity);
            }
        }



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

    @Transactional
    @Modifying
    public CateringStorageDTO createCateringStorage(CateringStorageDTO cateringStorageDTO) {
        log.info("Create catering storage {}", cateringStorageDTO);
        CateringStorageDTO createdStorage
                = CateringStorageDTO.fromEntity(cateringStorageRepository.save(CateringStorageDTO.toEntity(cateringStorageDTO)));


        log.info("Adding products to storage van. Products {}", cateringStorageDTO.getCateringStorageProducts());
        if(!cateringStorageDTO.getCateringStorageProducts().isEmpty()){
            cateringStorageService.addProductsToVanStorage(createdStorage.getCateringStorageId(),
                        new ArrayList<>(cateringStorageDTO.getCateringStorageProducts()));
        }

        CateringStorage cateringStorage = cateringStorageRepository.findById(createdStorage.getCateringStorageId()).orElseThrow(()
                -> new NotFoundException("Storage van not found with id: "
                + createdStorage.getCateringStorageId() + ". Cannot go over with the creation of the products"));

        return CateringStorageDTO.fromEntity(cateringStorage);
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
                expenseEvent.setEmployeeExpense(updatedExpenseEvent.isEmployeeExpense());
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
    public void setLoadQuantity(long workstationId, long cateringStorageId,
                                Map<Long, Double> insertValueMapProductIdAmountToInsert) {
        log.info("This map contain the product id and the amount" +
                " to add in order to perform a load product into workstation event. MAP: {} and it will be apply to a workstation with id {}. Unload than products from catering storage with id {}", insertValueMapProductIdAmountToInsert, workstationId, cateringStorageId);

        Workstation workstation = workstationRepository.findById(workstationId).orElseThrow(()
                -> new NotFoundException("Workstation not found for id " + workstationId));

        for (Product product : workstation.getProducts()) {
            long productId = product.getProductId();
            if (insertValueMapProductIdAmountToInsert.containsKey(productId)) {
                double quantityInserted = insertValueMapProductIdAmountToInsert.get(productId);
                product.setQuantityInserted(quantityInserted);
            }
        }

        //unload products amount from catering storage

//        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageId).orElseThrow(()
//                -> new NotFoundException("Storage not found for id " + cateringStorageId));
//
//        Set<Product> cateringStorageProducts = cateringStorage.getCateringStorageProducts();
//        for(Product product : cateringStorageProducts){
//
//            if(insertValueMapProductIdAmountToInsert.containsKey(product.getProductId())){
//                double newAmount = product.getQuantityInserted() - insertValueMapProductIdAmountToInsert.get(product.getProductId());
//                log.info("Set new value {} {} for product {} (with id {}) into storage catering {} with id {}",
//                        newAmount,
//                        product.getUnitMeasure(),
//                        product.getProductName(),
//                        product.getProductId(),
//                        cateringStorage.getName(),
//                        cateringStorageId);
//
//                product.setQuantityInserted(newAmount);
//            }
//        }
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

    @Transactional
    @Modifying
    public WorkstationDTO retrieveWorkstationById(long eventId, long workstationId) {

        log.info("Retrieve workstation with id {}. The event where this workstation belong has id {}",workstationId,  eventId);

        Workstation workstation = workstationRepository.findById(workstationId).orElseThrow(()
                -> new NotFoundException("Workstation not found for id " + workstationId));

        return WorkstationDTO.fromEntity(workstation);
    }

    @Transactional
    @Modifying
    public CateringStorage loadProductIntoStorageVan(
            long cateringStorageId,
            Map<Long, Double> insertValueMapProductIdAmountToInsert) {
        log.info("This map contain the product id and the amount to add in order to perform a load product into storage van event. " +
                "MAP: {} and it will be apply to a storage van with id {}", insertValueMapProductIdAmountToInsert, cateringStorageId);

        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageId).orElseThrow(()
                -> new NotFoundException("Storage van not found for id " + cateringStorageId));

        for (Product product : cateringStorage.getCateringStorageProducts()) {
            long productId = product.getProductId();
            if (insertValueMapProductIdAmountToInsert.containsKey(productId)) {
                double quantityInserted = insertValueMapProductIdAmountToInsert.get(productId);
                product.setQuantityInserted(quantityInserted);
            }
        }
        return cateringStorage;
    }

    @Transactional
    @Modifying
    public void deleteCateringStorage(String branchCode, long cateringStorageId) {

        //TODO: with the branch code u have to put back the quantity into the storage of that branch
        log.info("Delete catering storage with id {}", cateringStorageId);
        cateringStorageRepository.deleteById(cateringStorageId);
    }

    @Transactional
    @Modifying
    public void emptyCateringStorage(long cateringStorageId) {

        //TODO: with the branch code u have to put back the quantity into the storage of that branch
        log.info("Empty catering storage with id {}", cateringStorageId);
        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageId).orElseThrow(()
                -> new NotFoundException("Storage not found for id " + cateringStorageId));

        cateringStorage.getCateringStorageProducts().clear();

        cateringStorageRepository.save(cateringStorage);
    }

    @Transactional
    @Modifying
    public CateringStorageDTO updateCateringStorage(CateringStorageDTO cateringStorageDTO) {

        log.info("Update storage with id {} - New name {} - New licence plate {}", cateringStorageDTO.getCateringStorageId(), cateringStorageDTO.getName(), cateringStorageDTO.getCarLicensePlate());
        CateringStorage cateringStorage = cateringStorageRepository.findById(cateringStorageDTO.getCateringStorageId()).orElseThrow(()
                -> new NotFoundException("Storage not found for id " + cateringStorageDTO.getCateringStorageId()));

        cateringStorage.setName(cateringStorageDTO.getName());
        cateringStorage.setCarLicensePlate(cateringStorage.getCarLicensePlate());

        return CateringStorageDTO.fromEntity(cateringStorage);

    }

    @Transactional
    @Modifying
    public EventDTO updateEvent(long eventId,
                                String name,
                                String location) {

        log.info("Update event with id {} - Name {} , Location {}", eventId, name, location);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setName(name);
        event.setLocation(location);

        Event save = eventRepository.save(event);


        return EventDTO.fromEntity(save);
    }

    @Transactional
    @Modifying
    public WorkstationDTO updateWorkstationDetails(long eventId,
                                                   long workstationId,
                                                   String workstationName,
                                                   String workstationResponsable) {

        log.info("Update workstation with id {}, with name {} and responsable {}. Event id {} ",
                workstationId,
                workstationName,
                workstationResponsable,
                eventId);

        Workstation workstation = workstationRepository.findById(workstationId)
                .orElseThrow(() -> new NotFoundException("Workstation not found for id " + workstationId));

        workstation.setName(workstationName);
        workstation.setResponsable(workstationResponsable);

        Workstation savedWorkstation = workstationRepository.save(workstation);

        return WorkstationDTO.fromEntity(savedWorkstation);
    }
}
