package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.dto;

import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.entity.WaApiConfState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppConfigurationDTO {

    private long id;
    private String branchCode;
    private String phone;
    private String waApiInstanceId;
    private WaApiConfState waApiState;
    private String lastError;
    private LocalDateTime creationDate;
    private String qrCode;
    private String photoUrl;
    private String displayName;

    public WhatsAppConfiguration toEntity() {
        return WhatsAppConfiguration.builder()
                .id(this.id)
                .branchCode(this.branchCode)
                .phone(this.phone)
                .waApiInstanceId(this.waApiInstanceId)
                .creationDate(this.creationDate)
                .lastError(this.lastError)
                .qrCode(this.qrCode)
                .photoUrl(this.photoUrl)
                .displayName(this.displayName)
                .build();
    }

    public static WhatsAppConfigurationDTO fromEntity(WhatsAppConfiguration entity) {
        return WhatsAppConfigurationDTO.builder()
                .id(entity.getId())
                .branchCode(entity.getBranchCode())
                .phone(entity.getPhone())
                .waApiState(entity.getWaApiConfState())
                .waApiInstanceId(entity.getWaApiInstanceId())
                .creationDate(entity.getCreationDate())
                .lastError(entity.getLastError())
                .qrCode(entity.getQrCode())
                .photoUrl(entity.getPhotoUrl())
                .displayName(entity.getDisplayName())
                .build();
    }

    public static List<WhatsAppConfigurationDTO> fromEntityList(List<WhatsAppConfiguration> entities) {
        return entities.stream()
                .map(WhatsAppConfigurationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static List<WhatsAppConfiguration> toEntityList(List<WhatsAppConfigurationDTO> dtos) {
        return dtos.stream()
                .map(WhatsAppConfigurationDTO::toEntity)
                .collect(Collectors.toList());
    }
}
