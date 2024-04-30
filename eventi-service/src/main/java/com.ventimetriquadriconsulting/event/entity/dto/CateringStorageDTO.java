package com.ventimetriquadriconsulting.event.entity.dto;

import com.ventimetriquadriconsulting.event.entity.CateringStorage;
import com.ventimetriquadriconsulting.event.repository.CateringStorageRepository;
import com.ventimetriquadriconsulting.event.workstations.entity.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Builder
@ToString
public class CateringStorageDTO {
    private long cateringStorageId;
    private String branchCode;
    private Set<ProductDTO> cateringStorageProducts;

    public static CateringStorageDTO fromEntity(CateringStorage cateringStorage) {
        return CateringStorageDTO.builder()
                .cateringStorageId(cateringStorage.getCateringStorageId())
                .branchCode(cateringStorage.getBranchCode())
                .cateringStorageProducts(cateringStorage.getCateringStorageProducts().stream()
                        .map(ProductDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CateringStorage toEntity(CateringStorageDTO cateringStorageDTO) {
        return CateringStorage.builder()
                .cateringStorageId(cateringStorageDTO.getCateringStorageId())
                .branchCode(cateringStorageDTO.getBranchCode())
                .cateringStorageProducts(cateringStorageDTO.getCateringStorageProducts().stream()
                        .map(ProductDTO::toEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Set<CateringStorageDTO> fromEntityList(List<CateringStorage> cateringStorages) {
        return cateringStorages.stream()
                .map(CateringStorageDTO::fromEntity)
                .collect(Collectors.toSet());
    }

    public static Set<CateringStorage> toEntityList(Set<CateringStorageDTO> cateringStorageDTOs) {
        return cateringStorageDTOs.stream()
                .map(CateringStorageDTO::toEntity)
                .collect(Collectors.toSet());
    }

}
