package com.ventimetriquadriconsulting.event.entity;

import com.ventimetriquadriconsulting.event.workstations.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Entity(name = "CateringStorage")
@Table(name = "CATERING_STORAGE",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"catering_storage_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CateringStorage {

    @Id
    @SequenceGenerator(
            name = "catering_storage_id",
            sequenceName = "catering_storage_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "catering_storage_id"
    )
    @Column(
            name = "catering_storage_id",
            updatable = false
    )
    private long cateringStorageId;

    @Column(
            name = "branch_code",
            length = 10
    )
    private String branchCode;

    private String name;

    @Column(
            name = "car_licence_plate",
            length = 10
    )
    private String carLicensePlate;

    @ElementCollection
    @CollectionTable(
            name = "catering_products",
            joinColumns = @JoinColumn(name = "event_product_id")
    )
    @OrderColumn(name = "position")
    private Set<Product> cateringStorageProducts;
}
