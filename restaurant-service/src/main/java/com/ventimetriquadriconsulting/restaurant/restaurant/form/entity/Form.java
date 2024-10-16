package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @Column(name = "form_code", unique = true)
    private String formCode;

    private String formName;

    //name to display for the customers
    private String outputNameForCustomer;

    @Column(name = "branch_code", length = 10, nullable = false)
    private String branchCode;

    private String branchName;

    private String branchAddress;

    private String redirectPage;

    private ZonedDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Enumerated(EnumType.STRING)
    private FormStatus formStatus;

    @Lob
    @Column(name = "logo",
            columnDefinition = "TEXT")
    private byte[] logo;

    @OneToMany(mappedBy = "form",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<OpeningHours> regularOpeningHours = new ArrayList<>();

    @OneToMany(mappedBy = "form",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<SpecialDay> specialDays = new ArrayList<>();

    @OneToMany(mappedBy = "form",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Holidays> holidays = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "form_tags", joinColumns = @JoinColumn(name = "form_id"))
    @Column(name = "tag")
    private List<String> tag;

    @PrePersist
    public void prePersist() {
        this.tag = new ArrayList<>();

        this.creationDate = ZonedDateTime.now(ZoneId.of("CET"));
        this.setFormStatus(FormStatus.ATTIVO);
    }
}
