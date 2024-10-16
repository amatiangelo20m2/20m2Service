package com.ventimetriquadriconsulting.event.workstations.entity.dto;

import com.ventimetriquadriconsulting.event.workstations.entity.UnitMeasure;
import com.ventimetriquadriconsulting.event.workstations.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private long productId;
    private String productName;
    private double quantityInserted;
    private double quantityConsumed;
    private double price;
    private int vat;
    private UnitMeasure unitMeasure;

    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .quantityInserted(product.getQuantityInserted())
                .quantityConsumed(product.getQuantityConsumed())
                .price(product.getPrice())
                .vat(product.getVat())
                .unitMeasure(product.getUnitMeasure())
                .build();
    }

    public static Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .quantityInserted(productDTO.getQuantityInserted())
                .quantityConsumed(productDTO.getQuantityConsumed())
                .price(productDTO.getPrice())
                .vat(productDTO.getVat())
                .unitMeasure(productDTO.getUnitMeasure())
                .build();
    }

    public static List<ProductDTO> listFromEntities(List<Product> products) {
        return products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static Set<Product> listToEntities(List<ProductDTO> productDTOs) {
        return productDTOs.stream()
                .map(ProductDTO::toEntity)
                .collect(Collectors.toSet());
    }
}
