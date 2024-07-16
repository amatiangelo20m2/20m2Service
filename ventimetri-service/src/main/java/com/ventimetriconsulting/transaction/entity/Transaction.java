package com.ventimetriconsulting.transaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private String fromBranchCode;

    private String toBranchCode;

    private String fromUserCode;

    private TransactionType operationType;

    private boolean accepted;

    private LocalDate requestDate;

    private LocalTime requestTime;

}
