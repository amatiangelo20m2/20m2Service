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
    private String name;
    private String carLicensePlate;
    private Set<ProductDTO> cateringStorageProducts;

    public static CateringStorageDTO fromEntity(CateringStorage cateringStorage) {

        return CateringStorageDTO.builder()
                .cateringStorageId(cateringStorage.getCateringStorageId())
                .branchCode(cateringStorage.getBranchCode())
                .name(cateringStorage.getName())
                .carLicensePlate(cateringStorage.getCarLicensePlate())
                .cateringStorageProducts(cateringStorage.getCateringStorageProducts().stream()
                        .map(ProductDTO::fromEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CateringStorage toEntity(CateringStorageDTO cateringStorageDTO) {
        return CateringStorage.builder()
                .cateringStorageId(cateringStorageDTO.getCateringStorageId())
                .branchCode(cateringStorageDTO.getBranchCode())
                .name(cateringStorageDTO.getName())
                .carLicensePlate(cateringStorageDTO.getCarLicensePlate())
                .cateringStorageProducts(cateringStorageDTO.getCateringStorageProducts().stream()
                        .map(ProductDTO::toEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static List<CateringStorageDTO> fromEntityList(List<CateringStorage> cateringStorages) {
        return cateringStorages.stream()
                .map(CateringStorageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static Set<CateringStorage> toEntityList(Set<CateringStorageDTO> cateringStorageDTOs) {
        return cateringStorageDTOs.stream()
                .map(CateringStorageDTO::toEntity)
                .collect(Collectors.toSet());
    }

}
