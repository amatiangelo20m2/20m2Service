package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.Form;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.FormStatus;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.FormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {

    private Long formId;
    private String formCode;
    private String formName;
    private String redirectPage;
    private LocalDateTime creationDate;
    private String branchCode;
    private FormStatus formStatus;
    private List<String> tag;
    private FormType formType;
    // New field for base64-encoded image
//    private String imageData;

    // Conversion: Entity to DTO
    public static FormDTO fromEntity(Form form) {
        return FormDTO.builder()
                .formId(form.getFormId())
                .formCode(form.getFormCode())
                .formName(form.getFormName())
                .redirectPage(form.getRedirectPage())
                .creationDate(form.getCreationDate())
                .branchCode(form.getBranchCode())
                .tag(form.getTag())
                .formType(form.getFormType())
                .formStatus(form.getFormStatus())
//                .imageData(form.getImageData() != null ? Base64.getEncoder().encodeToString(form.getImageData()) : null)
                .build();
    }

    // Conversion: DTO to Entity
    public static Form toEntity(FormDTO formDTO) {
        return Form.builder()
                .formId(formDTO.getFormId())
                .formCode(formDTO.getFormCode())
                .formName(formDTO.getFormName())
                .redirectPage(formDTO.getRedirectPage())
                .creationDate(formDTO.getCreationDate())
                .branchCode(formDTO.getBranchCode())
                .tag(formDTO.getTag())
                .formType(formDTO.formType)
                .formStatus(formDTO.getFormStatus())
//                .imageData(formDTO.getImageData() != null ? Base64.getDecoder().decode(formDTO.getImageData()) : null)
                .build();
    }

    // List Conversion: List<Form> to List<FormDTO>
    public static List<FormDTO> fromEntityList(List<Form> formList) {
        return formList.stream().map(FormDTO::fromEntity).toList();
    }

    // List Conversion: List<FormDTO> to List<Form>
    public static List<Form> toEntityList(List<FormDTO> formDTOList) {
        return formDTOList.stream().map(FormDTO::toEntity).toList();
    }
}

