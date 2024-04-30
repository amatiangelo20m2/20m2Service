package com.ventimetriquadriconsulting.event.entity;

import com.ventimetriquadriconsulting.event.utils.EventStatus;
import com.ventimetriquadriconsulting.event.workstations.entity.Workstation;
import com.ventimetriquadriconsulting.event.workstations.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity(name = "Event")
@Table(name = "EVENT",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"event_id", "name"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event implements Serializable {


    @Id
    @SequenceGenerator(
            name = "event_id",
            sequenceName = "event_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_id"
    )
    @Column(
            name = "event_id",
            updatable = false
    )
    private long eventId;

    private String name;
    private String createdBy;
    private LocalDate dateEvent;
    private LocalDate dateCreation;

    @Enumerated
    private EventStatus eventStatus;

    @Column(
            name = "branch_code",
            length = 10
    )
    private String branchCode;
    private String location;

    @OneToMany(mappedBy = "event",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private Set<Workstation> workstations;

    @ElementCollection
    @CollectionTable(
            name = "expences_event",
            joinColumns = @JoinColumn(name = "expences_event_id")
    )
    @OrderColumn(name = "position")
    private Set<ExpenseEvent> expenseEvents;

}
