package com.ventimetriconsulting.supplier.dto;

import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private long productId;
    private String name;
    private String productCode;
    private UnitMeasure unitMeasure;
    private String description;
    private int vatApplied;
    private double price;
    private double vatPrice;
    private String category;
    private String sku;
    private boolean available;
    private boolean deleted;
    private List<String> branchListNotAllowedToSeeThisProduct;

    public static Product fromDTO(ProductDTO productDTO) {
        return Product.builder()
                .productId(0)
                .name(productDTO.getName())
                .unitMeasure(productDTO.getUnitMeasure())
                .vatApplied(productDTO.getVatApplied())
                .price(productDTO.getPrice())
                .vatPrice(productDTO.getVatPrice())
                .productCode(productDTO.getProductCode())
                .description(productDTO.getDescription())
                .category(productDTO.getCategory())
                .sku(productDTO.getSku())
                .available(productDTO.isAvailable())
                .deleted(productDTO.isDeleted())
                .branchListNotAllowedToSeeThisProduct(productDTO.getBranchListNotAllowedToSeeThisProduct())
                .build();
    }

    public static ProductDTO toDTO(Product product) {


        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .unitMeasure(product.getUnitMeasure())
                .vatApplied(product.getVatApplied())
                .price(product.getPrice())
                .vatPrice(product.getVatPrice())
                .productCode(product.getProductCode())
                .description(product.getDescription())
                .category(product.getCategory())
                .sku(product.getSku())
                .available(product.isAvailable())
                .deleted(product.isDeleted())
                .branchListNotAllowedToSeeThisProduct(product.getBranchListNotAllowedToSeeThisProduct())
                .build();
    }
    public static List<Product> fromDTOList(List<ProductDTO> productDTOList) {
        return productDTOList.stream()
                    .map(ProductDTO::fromDTO)
                    .collect(Collectors.toList());
    }
    public static List<ProductDTO> toDTOList(List<Product> productList, boolean available, boolean deleted) {
        return productList.stream()
                .filter(product -> product.isAvailable() == available
                        && product.isDeleted() == deleted)
                .map(ProductDTO::toDTO)
                .collect(Collectors.toList());
    }
}
