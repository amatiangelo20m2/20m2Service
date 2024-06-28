package com.ventimetriquadriconsulting.event.entity.dto;

import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.WorkstationDTO;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Builder
@ToString
public class EventDTO implements Serializable {

    private long eventId;
    private String name;
    private String createdBy;
    private LocalDate dateEvent;
    private LocalDate dateCreation;
    private EventStatus eventStatus;
    private String branchCode;
    private String location;
    private Set<WorkstationDTO> workstations;
    private Set<ExpenseEventDTO> expenseEvents;
    private long cateringStorageId;

    public static EventDTO fromEntity(Event event) {
        return EventDTO.builder()
                .eventId(event.getEventId())
                .name(event.getName())
                .createdBy(event.getCreatedBy())
                .dateEvent(event.getDateEvent())
                .dateCreation(event.getDateCreation())
                .eventStatus(event.getEventStatus())
                .branchCode(event.getBranchCode())
                .location(event.getLocation())
                .workstations(event.getWorkstations().stream()
                        .map(WorkstationDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .expenseEvents(new HashSet<>(ExpenseEventDTO.toDTOList(event.getExpenseEvents().stream().toList())))
                .cateringStorageId(event.getCateringStorageId())
                .build();
    }

    public static Event toEntity(EventDTO eventDTO) {
        return Event.builder()
                .eventId(eventDTO.getEventId())
                .name(eventDTO.getName())
                .createdBy(eventDTO.getCreatedBy())
                .dateEvent(eventDTO.getDateEvent())
                .dateCreation(eventDTO.getDateCreation())
                .eventStatus(eventDTO.getEventStatus())
                .branchCode(eventDTO.getBranchCode())
                .location(eventDTO.getLocation())
                .workstations(eventDTO.getWorkstations() != null ?
                        eventDTO.getWorkstations().stream()
                                .map(WorkstationDTO::toEntity)
                                .collect(Collectors.toSet()) :
                        Collections.emptySet())
                .expenseEvents(eventDTO.getExpenseEvents() != null ?
                        ExpenseEventDTO.fromDTOList(eventDTO.getExpenseEvents().stream().toList()) :
                        Collections.emptySet())
                .cateringStorageId(eventDTO.getCateringStorageId())
                .build();
    }

    public static List<EventDTO> listFromEntities(Set<Event> events) {
        return events.stream()
                .map(EventDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static Set<Event> listToEntities(Set<EventDTO> eventDTOs) {
        return eventDTOs.stream()
                .map(EventDTO::toEntity)
                .collect(Collectors.toSet());
    }


}