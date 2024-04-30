package com.ventimetriconsulting;


import com.ventimetriquadriconsulting.event.EventiServiceApplication;
import com.ventimetriquadriconsulting.event.controller.EventController;
import com.ventimetriquadriconsulting.event.entity.dto.EventDTO;
import com.ventimetriquadriconsulting.event.entity.dto.ExpenseEventDTO;
import com.ventimetriquadriconsulting.event.repository.CateringStorageRepository;
import com.ventimetriquadriconsulting.event.repository.EventRepository;
import com.ventimetriquadriconsulting.event.service.CateringStorageService;
import com.ventimetriquadriconsulting.event.service.EventService;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.utils.WorkstationType;
import com.ventimetriquadriconsulting.event.workstations.entity.UnitMeasure;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationDTO;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import com.ventimetriquadriconsulting.event.workstations.repository.WorkstationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes = EventiServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class TestSuiteEvent {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    private EventController eventController;

    @Autowired
    private CateringStorageRepository cateringStorageRepository;
    @BeforeEach
    public void init(){

        CateringStorageService cateringStorageService = new CateringStorageService(cateringStorageRepository);
        EventService eventService = new EventService(eventRepository, workstationRepository);
        eventController = new EventController(eventService, cateringStorageService);
    }

    @Test
    public void testEventServiceCreateEventWithNullWorkstationsAnsExpenses(){
        ResponseEntity<EventDTO> save = eventController
                .save(createFakeEventDTO("BASDASDASD", null,
                null));

        assertEquals(Objects.requireNonNull(save.getBody()).getName(), "Fake Event");
        assertEquals(save.getBody().getBranchCode(), "BASDASDASD");
        assertEquals(save.getBody().getLocation(), "Fake Location");
        assertEquals(save.getBody().getWorkstations().size(), 0);
        assertEquals(save.getBody().getExpenseEvents().size(), 0);
    }

    @Test
    public void testEventServiceCreateEventWithWorkstations(){
        ResponseEntity<EventDTO> save = eventController.save(
                createFakeEventDTO(
                        "BASDASDASD",
                        generateFakeWorkstations(2),
                        null));


        assertEquals(Objects.requireNonNull(save.getBody()).getName(), "Fake Event");
        assertEquals(save.getBody().getBranchCode(), "BASDASDASD");
        assertEquals(save.getBody().getLocation(), "Fake Location");
        assertEquals(2, save.getBody().getWorkstations().size());

        log.info("Worksations: {}", save.getBody().getWorkstations().toString());
        assertEquals(0, save.getBody().getExpenseEvents().size());

        ResponseEntity<List<EventDTO>> eventsByBranchCode = eventController.retrieveEventsByBranchCode("BASDASDASD", EventStatus.APERTO);

        assertEquals(Objects.requireNonNull(eventsByBranchCode.getBody()).size(), 1);
        assertEquals(eventsByBranchCode.getBody().stream().toList().get(0).getWorkstations().size(), 2);
        assertEquals(eventsByBranchCode.getBody().stream().toList().get(0).getWorkstations().stream().toList()
                .get(0).getWorkstationProducts().size(), 10);

        long workstationId = eventsByBranchCode.getBody().stream().toList().get(0)
                .getWorkstations().stream().toList().get(0).getWorkstationId();

        long productId = eventsByBranchCode.getBody().stream().toList().get(0)
                .getWorkstations().stream().toList().get(0).getWorkstationProducts().stream().toList().get(2).getProductId();

        eventController.deleteWorkstationProduct(workstationId, productId);


        eventsByBranchCode = eventController.retrieveEventsByBranchCode("BASDASDASD", EventStatus.APERTO);

        assertEquals(Objects.requireNonNull(eventsByBranchCode.getBody()).size(), 1);
        assertEquals(eventsByBranchCode.getBody().stream().toList().get(0).getWorkstations().size(), 2);
        assertEquals(eventsByBranchCode.getBody().stream().toList().get(0).getWorkstations().stream().toList()
                .get(0).getWorkstationProducts().size(), 9);



    }

    @Test
    public void testUpdateProductQuantityForWorkstation() {
        // Create a fake event with a workstation
        EventDTO fakeEventDTO = createFakeEventDTO(
                "BASDASDASD",
                generateFakeWorkstations(1),
                null);

        // Save the fake event
        ResponseEntity<EventDTO> saveResponse = eventController.save(fakeEventDTO);
        assertEquals(200, saveResponse.getStatusCodeValue());

        // Retrieve the saved event to get its ID
        long eventId = Objects.requireNonNull(saveResponse.getBody()).getEventId();

        // Retrieve the workstation ID from the saved event
        long workstationId = Objects.requireNonNull(saveResponse.getBody()).getWorkstations().stream()
                .findFirst().orElseThrow().getWorkstationId();

        // Create a map of product IDs and updated quantities
        Map<Long, Double> updatedQuantities = new HashMap<>();
        updatedQuantities.put(1L, 50.0); // Assuming product with ID 1 is present in the workstation

        // Update the product quantities for the workstation
        ResponseEntity<?> updateResponse = eventController.updateProductInsertedQuantityForWorkstation(eventId, workstationId, updatedQuantities);
        assertEquals(HttpStatusCode.valueOf(200), updateResponse.getStatusCode());

        // Retrieve the updated event to check the product quantities
        ResponseEntity<List<EventDTO>> eventsByBranchCode = eventController.retrieveEventsByBranchCode("BASDASDASD", EventStatus.APERTO);
        assertEquals(1, Objects.requireNonNull(eventsByBranchCode.getBody()).size());

        // Retrieve the updated workstation
        WorkstationDTO updatedWorkstation = eventsByBranchCode.getBody().stream()
                .findFirst().orElseThrow().getWorkstations().stream()
                .filter(ws -> ws.getWorkstationId() == workstationId)
                .findFirst().orElseThrow();

        // Retrieve the updated product from the workstation
        ProductDTO updatedProduct = updatedWorkstation.getWorkstationProducts().stream()
                .filter(product -> product.getProductId() == 1L)
                .findFirst().orElseThrow();

        // Check if the quantity was updated correctly
        assertEquals(50.0, updatedProduct.getQuantityInserted());
    }

    public static EventDTO createFakeEventDTO(String branchCode, Set<WorkstationDTO> workstationDTOList,
                                              Set<ExpenseEventDTO> expenseEventDTOList) {
        return EventDTO.builder()
                .eventId(1L)
                .name("Fake Event")
                .createdBy("Test User")
                .dateEvent(LocalDate.of(2024, 5, 1))
                .dateCreation(LocalDate.now())
                .eventStatus(EventStatus.APERTO)
                .branchCode(branchCode)
                .location("Fake Location")
                .workstations(workstationDTOList)
                .expenseEvents(expenseEventDTOList)
                .build();
    }

    public static Set<WorkstationDTO> generateFakeWorkstations(int numberOfWorkstations) {
        List<WorkstationDTO> workstations = new ArrayList<>();
        for (int i = 0; i < numberOfWorkstations; i++) {
            WorkstationDTO workstation = WorkstationDTO.builder()
                    .workstationId(0)
                    .name("Workstation " + (i + 1))
                    .responsable("Responsible " + (i + 1))
                    .workstationType(WorkstationType.BAR)
                    .workstationProducts(generateFakeWorkstationProducts(10))
                    .build();
            workstations.add(workstation);
        }
        return new HashSet<>(workstations);
    }

    private static Set<ProductDTO> generateFakeWorkstationProducts(int prodNumber) {
        Set<ProductDTO> workstationProducts = new HashSet<>();
        Random random = new Random();

        for (int i = 0; i < prodNumber; i++) {
            ProductDTO product = ProductDTO.builder()
                    .productId(i + 1)
                    .productName("Product " + (i + 1))
                    .quantityInserted(random.nextDouble() * 100)
                    .quantityConsumed(0)
                    .price(random.nextDouble() * 1000)
                    .unitMeasure(getRandomUnitMeasure())
                    .build();
            workstationProducts.add(product);
        }
        return workstationProducts;
    }

    public static UnitMeasure getRandomUnitMeasure() {
        UnitMeasure[] unitMeasures = UnitMeasure.values(); // Get all enum values
        Random random = new Random();
        int randomIndex = random.nextInt(unitMeasures.length); // Generate random index
        return unitMeasures[randomIndex]; // Return enum value at random index
    }
}
