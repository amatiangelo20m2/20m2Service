package com.ventimetriconsulting.storage.entity.dto;

import com.ventimetriconsulting.storage.entity.Inventario;
import com.ventimetriconsulting.storage.entity.extra.InventoryAction;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private long inventarioId;
    private LocalDate insertionDate;
    private LocalDate deletionDate;
    private ProductDTO productDTO;
    private Set<InventoryAction> inventoryAction;
    private double stock;

    public static InventarioDTO fromEntity(Inventario inventario) {
        return InventarioDTO.builder()
                .insertionDate(inventario.getInsertionDate())
                .inventarioId(inventario.getInventarioId())
                .deletionDate(inventario.getDeletionDate())
                .inventoryAction(inventario.getInventoryActions())
                .productDTO(ProductDTO.toDTO(inventario.getProduct()))
                .stock(inventario.getStock())
                .build();
    }

    public static Set<InventarioDTO> fromEntities(Set<Inventario> inventarios) {
        return inventarios.stream()
                .map(InventarioDTO::fromEntity)
                .sorted(Comparator.comparing(dto -> dto.getProductDTO().getName().toLowerCase(), Comparator.naturalOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Inventario toEntity(InventarioDTO inventarioDTO) {
        Inventario inventario = new Inventario();
        inventario.setInventarioId(inventarioDTO.getInventarioId());
        inventario.setInsertionDate(inventarioDTO.getInsertionDate());
        inventario.setDeletionDate(inventarioDTO.getDeletionDate());
        inventario.setInventoryActions(inventarioDTO.getInventoryAction());
        inventario.setProduct(ProductDTO.fromDTO(inventarioDTO.getProductDTO()));
        inventario.setStock(inventarioDTO.getStock());
        return inventario;
    }

    public static Set<Inventario> toEntities(Set<InventarioDTO> inventarioDTOs) {
        Set<Inventario> inventarios = new HashSet<>();
        for (InventarioDTO inventarioDTO : inventarioDTOs) {
            inventarios.add(toEntity(inventarioDTO));
        }
        return inventarios;
    }

}
