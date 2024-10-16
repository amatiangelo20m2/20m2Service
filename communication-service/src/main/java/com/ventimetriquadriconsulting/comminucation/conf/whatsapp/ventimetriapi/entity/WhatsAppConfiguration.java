package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.entity.WaApiConfState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity(name = "WhatsAppConfiguration")
@Table(name = "WHATSAPP_CONFIGURATION", uniqueConstraints=@UniqueConstraint(columnNames={"phone", "branch_code"}))
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class WhatsAppConfiguration {

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

    @Column(name = "branch_code", length = 10, nullable = false, unique = true)
    private String branchCode;

    @Column(name= "phone")
    private String phone;
//
    @Enumerated(EnumType.STRING)
    private WaApiConfState waApiConfState;

    @Column(name= "waapi_istance_id")
    private String waApiInstanceId;

    @Column(name = "last_error")
    private String lastError;

    @Lob
    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "display_name")
    private String displayName;

    @Column
    private LocalDateTime creationDate;
        
    @PrePersist
    public void beforePersist(){
        ZonedDateTime nowInItaly = ZonedDateTime.now(ZoneId.of("CET"));
        this.creationDate = nowInItaly.toLocalDateTime();
    }

}
