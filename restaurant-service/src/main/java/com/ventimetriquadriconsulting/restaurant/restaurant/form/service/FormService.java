package com.ventimetriquadriconsulting.restaurant.restaurant.form.service;

import com.ventimetriquadriconsulting.restaurant.exception.customexception.FormNotFoundException;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.Restaurant;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.repository.RestaurantRepository;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.service.RestaurantService;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.Form;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto.FormDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.repository.FormRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
@Slf4j
@AllArgsConstructor
public class FormService {

    private FormRepository formRepository;
    private RestaurantService restaurantService;

    public List<FormDTO> retrieveFormByBranchCode(String branchCode) {
        log.info("Retrieve form by branch code [{}]", branchCode);
        Optional<List<Form>> byBranchCode = formRepository.findByBranchCode(branchCode);
        return byBranchCode.map(FormDTO::fromEntityList).orElseGet(ArrayList::new);
    }

    public FormDTO retrieveFormByFormCode(String formCode) {
        log.info("Retrieve form by form code [{}]", formCode);
        Form form = formRepository.findByFormCode(formCode)
                .orElseThrow(() -> new FormNotFoundException("Form con codice [" + formCode + "] non trovato"));
        return FormDTO.fromEntity(form);
    }

    @Transactional
    public FormDTO createForm(FormDTO formDTO) {

        log.info("Create a new form. Form Data [{}]", formDTO);
//        formDTO.setFormCode(UUID.randomUUID().toString());

        RestaurantDTO restaurantDTO = restaurantService.retrieveRestaurantConfiguration(formDTO.getBranchCode());

        formDTO.setBranchCode(restaurantDTO.getBranchCode());

        Form savedForm = formRepository.save(FormDTO.toEntity(formDTO));
        log.info("New form created {}", savedForm);
        return FormDTO.fromEntity(savedForm);
    }

    @Transactional
    @Modifying
    public FormDTO editForm(FormDTO formDTO) {
        log.info("Update the form with code {} to a form with the following details {}", formDTO.getFormCode(), formDTO);
        Form form = formRepository.findByFormCode(formDTO.getFormCode())
                .orElseThrow(() -> new FormNotFoundException("Form con codice [" + formDTO.getFormCode() + "] non trovato"));


        form.setFormName(formDTO.getFormName());
        form.setTag(formDTO.getTag());
        form.setFormStatus(formDTO.getFormStatus());
        form.setRedirectPage(formDTO.getRedirectPage());

        Form savedForm = formRepository.save(form);
        return FormDTO.fromEntity(savedForm);
    }

    public List<FormDTO> retrieveAll() {
        log.info("Retrieve all forms");
        List<Form> forms = formRepository.findAll();
        return FormDTO.fromEntityList(forms);
    }
}
