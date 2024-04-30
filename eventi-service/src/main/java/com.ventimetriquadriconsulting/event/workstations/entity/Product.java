package com.ventimetriquadriconsulting.event.workstations.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product implements Serializable {

    private long productId;
    private String productName;
    private double quantityInserted;
    private double quantityConsumed;
    private double price;
    private UnitMeasure unitMeasure;
}
