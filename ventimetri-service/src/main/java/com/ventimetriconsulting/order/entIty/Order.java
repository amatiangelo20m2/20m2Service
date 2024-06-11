package com.ventimetriconsulting.order.entIty;


import com.ventimetriconsulting.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Order_Entity")
@Table(name = "order_entity",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"order_id"}))
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(
            name = "order_id",
            sequenceName = "order_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_id"
    )
    @Column(
            name = "order_id",
            updatable = false
    )
    private long orderId;

    private String createdByUser;
    private String userCode;
    private String createdByBranchName;

    private LocalDate insertedDate;
    private LocalDate incomingDate;

    private LocalTime preferredReceivingHour;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "order_target", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderTarget orderTarget;

    private String codeTarget;
    private String nameTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = true)
    private Branch branch;

    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @OrderColumn(name = "position")
    private Set<OrderItem> orderItems;


//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<OrderItem> orderItems;
//
    public Set<OrderItem> getOrderItems() {
        if (this.orderItems == null) {
            this.orderItems = new HashSet<>();
        }
        return this.orderItems;
    }
}
