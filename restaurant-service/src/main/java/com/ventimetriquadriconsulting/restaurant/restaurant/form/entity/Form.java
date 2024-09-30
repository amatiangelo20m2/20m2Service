package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "forms")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Form {

    @Id
    @SequenceGenerator(
            name = "form_id",
            sequenceName = "form_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "form_id"
    )
    @Column(
            name = "form_id",
            updatable = false
    )
    private Long formId;

    private String formCode;

    private String formName;

    private String redirectPage;

    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Enumerated(EnumType.STRING)
    private FormStatus formStatus;

//    @Lob  // Large Object annotation for binary data
//    @Column(name = "image_data", columnDefinition = "BYTEA")
//    private byte[] imageData;

    @Column(name = "branch_code", length = 10, nullable = false)
    private String branchCode;

    @ElementCollection
    @CollectionTable(name = "form_tags", joinColumns = @JoinColumn(name = "form_id"))
    @Column(name = "tag")
    private List<String> tag;

    @PrePersist
    public void generateUniqueCode() {

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 8;
        Random random = new SecureRandom();

        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        this.formCode = code.toString();


        this.tag = new ArrayList<>();
        ZonedDateTime nowInItaly = ZonedDateTime.now(ZoneId.of("CET"));
        this.creationDate = nowInItaly.toLocalDateTime();
        this.setFormStatus(FormStatus.ATTIVO);

    }
}
