package com.ventimetriconsulting.order.entIty;


import com.ventimetriconsulting.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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

    private String createdBy;
    private String userCode;

    private LocalDate insertedDate;
    private LocalDate incomingDate;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "order_target", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderTarget orderTarget;

    //valorized if orderTarget is SUPPLIER
    private String supplierCodeTarget;

    //valorized if orderTarget is BRANCH
    private String branchCodeTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = true)
    private Branch branch;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrderItem> orderItems;

    public Set<OrderItem> getOrderItems() {
        if (this.orderItems == null) {
            this.orderItems = new HashSet<>();
        }
        return this.orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", createdBy='" + createdBy + '\'' +
                ", userCode='" + userCode + '\'' +
                ", insertedDate=" + insertedDate +
                ", incomingDate=" + incomingDate +
                ", orderStatus=" + orderStatus +
                ", orderTarget=" + orderTarget +
                ", supplierCodeTarget='" + supplierCodeTarget + '\'' +
                ", branchCodeTarget='" + branchCodeTarget + '\'' +
                ", branch=" + branch +
                '}';
    }
}
