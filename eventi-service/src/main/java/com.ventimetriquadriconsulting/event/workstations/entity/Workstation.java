package com.ventimetriquadriconsulting.event.workstations.entity;

import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.utils.WorkstationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity(name = "Workstation")
@Table(name = "WORKSTATION",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"workstation_id", "name"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Workstation {

    @Id
    @SequenceGenerator(
            name = "workstation_id",
            sequenceName = "workstation_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "workstation_id"
    )
    @Column(
            name = "workstation_id",
            updatable = false
    )
    private long workstationId;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;
    private String responsable;


    @Enumerated
    private WorkstationType workstationType;

    @ElementCollection
    @CollectionTable(
            name = "workstation_products",
            joinColumns = @JoinColumn(name = "workstation_product_id")
    )
    @OrderColumn(name = "position")
    private Set<Product> products;

    @Override
    public String toString() {
        return "Workstation{" +
                "workstationId=" + workstationId +
                ", name='" + name + '\'' +
                ", responsable='" + responsable + '\'' +
                ", workstationType=" + workstationType +
                ", products=" + products +
                '}';
    }
}
