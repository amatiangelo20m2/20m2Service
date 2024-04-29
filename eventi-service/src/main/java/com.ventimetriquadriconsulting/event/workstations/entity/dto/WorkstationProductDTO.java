package com.ventimetriquadriconsulting.event.workstations.entity.dto;

import com.ventimetriquadriconsulting.event.workstations.entity.UnitMeasure;
import com.ventimetriquadriconsulting.event.workstations.entity.WorkstationProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkstationProductDTO {

    private long productId;
    private String productName;
    private double quantityInserted;
    private double quantityConsumed;
    private double price;
    private UnitMeasure unitMeasure;

    public static WorkstationProductDTO fromEntity(WorkstationProduct product) {
        return WorkstationProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .quantityInserted(product.getQuantityInserted())
                .quantityConsumed(product.getQuantityConsumed())
                .price(product.getPrice())
                .unitMeasure(product.getUnitMeasure())
                .build();
    }

    public static WorkstationProduct toEntity(WorkstationProductDTO productDTO) {
        return WorkstationProduct.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .quantityInserted(productDTO.getQuantityInserted())
                .quantityConsumed(productDTO.getQuantityConsumed())
                .price(productDTO.getPrice())
                .unitMeasure(productDTO.getUnitMeasure())
                .build();
    }

    public static Set<WorkstationProductDTO> listFromEntities(Set<WorkstationProduct> products) {
        return products.stream()
                .map(WorkstationProductDTO::fromEntity)
                .collect(Collectors.toSet());
    }

    public static Set<WorkstationProduct> listToEntities(Set<WorkstationProductDTO> productDTOs) {
        return productDTOs.stream()
                .map(WorkstationProductDTO::toEntity)
                .collect(Collectors.toSet());
    }
}
