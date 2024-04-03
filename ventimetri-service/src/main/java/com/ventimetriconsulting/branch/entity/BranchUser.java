package com.ventimetriconsulting.branch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity(name = "BranchUser")
@Table(name = "BRANCH_USER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"user_code", "branch_code"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchUser {

    @Id
    @SequenceGenerator(
            name = "id",
            sequenceName = "id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "id"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_code")
    private Branch branch;

    @Size(min = 10, max = 10)
    @Column(name = "user_code")
    private String userCode;

    @Column(name = "role")
    private Role role;

    //to this token we will send the notification push in case they have app installed
    @Column(name = "fmc_token")
    private String fMCToken;

    @Column(name = "authorized")
    private boolean authorized;
}
