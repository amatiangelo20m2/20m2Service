package com.ventimetriconsulting.supplier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "Product")
@Table(name = "product",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "unitMeasure", "supplier_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Product {

    @Id
    @SequenceGenerator(
            name = "product_id",
            sequenceName = "product_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id"
    )
    @Column(
            name = "product_id",
            updatable = false
    )
    private long productId;

    private String name;

    @Column(
            name = "product_code",
            nullable = false,
            unique = true,
            length = 20
    )
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "unitMeasure",
            nullable = false
    )
    private UnitMeasure unitMeasure;
    private String description;
    private int vatApplied;
    private double price;
    private double vatPrice = 0.0;
    private ProductCategory category;
    private String sku;
    private boolean available = true;
    private boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "branch_list_not_allowed_to_see_product", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "branch_list_not_allowed_to_see_product")
    private List<String> branchListNotAllowedToSeeThisProduct;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "supplier_id")
    @JsonIgnore
    private Supplier supplier;

    @PrePersist
    public void generateUniqueCode() {
        this.productCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "PRO" + uuid.substring(0, 17).toUpperCase();
    }

}
