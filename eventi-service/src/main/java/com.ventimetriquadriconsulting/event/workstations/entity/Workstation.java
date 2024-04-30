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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workstation that = (Workstation) o;
        return workstationId == that.workstationId && Objects.equals(name, that.name) && Objects.equals(responsable, that.responsable) && Objects.equals(event, that.event) && workstationType == that.workstationType && Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workstationId, name, responsable, event, workstationType, products);
    }
}
