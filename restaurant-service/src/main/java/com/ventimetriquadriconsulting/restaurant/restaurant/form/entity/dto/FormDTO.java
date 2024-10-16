package com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.Form;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.FormStatus;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.FormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDTO {

    private Long formId;
    private String formCode;
    private String formName;
    private String outputNameForCustomer;
    private String branchCode;
    private String branchName;
    private String branchAddress;
    private String redirectPage;
    private ZonedDateTime creationDate;
    private FormType formType;
    private FormStatus formStatus;
    private byte[] logo;
    private List<OpeningHoursDTO> regularOpeningHours;
    private List<SpecialDayDTO> specialDays;
    private List<HolidaysDTO> holidays;
    private List<String> tag;

    public static FormDTO fromEntity(Form form) {
        if (form == null) {
            return null;
        }

        return FormDTO.builder()
                .formId(form.getFormId())
                .formCode(form.getFormCode())
                .formName(form.getFormName())
                .outputNameForCustomer(form.getOutputNameForCustomer())
                .branchCode(form.getBranchCode())
                .branchName(form.getBranchName())
                .branchAddress(form.getBranchAddress())
                .redirectPage(form.getRedirectPage())
                .creationDate(form.getCreationDate())
                .formType(form.getFormType())
                .formStatus(form.getFormStatus())
                .logo(form.getLogo())
                .regularOpeningHours(form.getRegularOpeningHours().stream()
                        .map(OpeningHoursDTO::fromEntity)
                        .collect(Collectors.toList()))
                .specialDays(form.getSpecialDays().stream()
                        .map(SpecialDayDTO::fromEntity)
                        .collect(Collectors.toList()))
                .holidays(form.getHolidays().stream()
                        .map(HolidaysDTO::fromEntity)
                        .collect(Collectors.toList()))
                .tag(form.getTag())
                .build();
    }

    public Form toEntity() {
        Form form = new Form();
        form.setFormId(this.formId);
        form.setFormCode(this.formCode);
        form.setFormName(this.formName);
        form.setOutputNameForCustomer(this.outputNameForCustomer);
        form.setBranchCode(this.branchCode);
        form.setBranchName(this.branchName);
        form.setBranchAddress(this.branchAddress);
        form.setRedirectPage(this.redirectPage);
        form.setCreationDate(this.creationDate);

        // Assume formType and formStatus are enums, adjust accordingly
        form.setFormType(this.formType);
        form.setFormStatus(this.formStatus);
        form.setLogo(this.logo);

        // Null-safe handling of regularOpeningHours
        form.setRegularOpeningHours(this.regularOpeningHours != null ?
                this.regularOpeningHours.stream()
                        .map(OpeningHoursDTO::toEntity)
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        // Null-safe handling of specialDays
        form.setSpecialDays(this.specialDays != null ?
                this.specialDays.stream()
                        .map(SpecialDayDTO::toEntity)
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        // Null-safe handling of holidays
        form.setHolidays(this.holidays != null ?
                this.holidays.stream()
                        .map(HolidaysDTO::toEntity)
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        // Null-safe handling of tags
        form.setTag(this.tag != null ? this.tag : new ArrayList<>());

        return form;
    }


    public static List<FormDTO> fromEntityList(List<Form> forms) {
        return forms.stream()
                .map(FormDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Method to convert list of DTOs to list of entities
    public static List<Form> toEntityList(List<FormDTO> formDTOS) {
        return formDTOS.stream()
                .map(FormDTO::toEntity)
                .collect(Collectors.toList());
    }
}