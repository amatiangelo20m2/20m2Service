package com.ventimetriconsulting.transaction.entity;

import com.ventimetriconsulting.inventario.entity.extra.OperationType;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Transaction")
@Table(name = "transaction",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"transaction_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Transaction {

    @Id
    @SequenceGenerator(
            name = "transaction_id",
            sequenceName = "transaction_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_id"
    )
    @Column(
            name = "transaction_id",
            updatable = false
    )
    private long transactionId;

    private String branchCode;

    private String fromUserCode;

    private String toUserCode;

    private TransactionType operationType;

    private boolean accepted;

}
