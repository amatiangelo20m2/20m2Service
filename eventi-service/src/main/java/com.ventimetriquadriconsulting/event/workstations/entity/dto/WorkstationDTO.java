package com.ventimetriquadriconsulting.event.workstations.entity.dto;

import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.utils.WorkstationType;
import com.ventimetriquadriconsulting.event.workstations.entity.Workstation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Builder
@ToString
public class WorkstationDTO implements Serializable {

    private long workstationId;
    private String name;
    private String responsable;
    private WorkstationType workstationType;
    private Set<WorkstationProductDTO> workstationProducts;

    public static WorkstationDTO fromEntity(Workstation workstation) {
        return WorkstationDTO.builder()
                .workstationId(workstation.getWorkstationId())
                .name(workstation.getName())
                .responsable(workstation.getResponsable())
                .workstationType(workstation.getWorkstationType())
                .workstationProducts(workstation.getWorkstationProducts().stream()
                        .map(WorkstationProductDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Workstation toEntity(WorkstationDTO workstationDTO) {
        return Workstation.builder()
                .workstationId(workstationDTO.getWorkstationId())
                .name(workstationDTO.getName())
                .responsable(workstationDTO.getResponsable())
                .workstationType(workstationDTO.getWorkstationType())
                .workstationProducts(workstationDTO.getWorkstationProducts().stream()
                        .map(WorkstationProductDTO::toEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Set<WorkstationDTO> listFromEntities(Set<Workstation> workstations) {
        return workstations.stream()
                .map(WorkstationDTO::fromEntity)
                .collect(Collectors.toSet());
    }

    public static Set<Workstation> listToEntities(Set<WorkstationDTO> workstationDTOs) {
        return workstationDTOs.stream()
                .map(WorkstationDTO::toEntity)
                .collect(Collectors.toSet());
    }
}
