package com.ventimetriquadriconsulting.restaurant.restaurant.form.service;

import com.ventimetriquadriconsulting.restaurant.exception.customexception.FormNotFoundException;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.configuration.service.RestaurantService;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.*;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto.FormDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.repository.FormRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
@Service
@Slf4j
@AllArgsConstructor
public class FormService {

    private FormRepository formRepository;
    private RestaurantService restaurantService;
    //
    @Transactional
    public FormDTO createForm(FormDTO formDTO) {

        log.info("Create a new form. Form Data [{}]", formDTO);

        RestaurantDTO restaurantDTO = restaurantService
                .retrieveRestaurantConfiguration(formDTO.getBranchCode());

        formDTO.setBranchCode(restaurantDTO.getBranchCode());
        formDTO.setFormCode(buildUniqueFormCode());

        Form savedForm = formRepository.save(formDTO.toEntity());

        if (formDTO.getFormType() == FormType.PRENOTAZIONE) {
            List<OpeningHours> openingHoursList = new ArrayList<>();
            for (DayOfWeek day : DayOfWeek.values()) {
                TimeRange timeRange = TimeRange.builder()
                        .openingHour(12)
                        .openingMinutes(0)
                        .closingHour(18)
                        .closingMinutes(0)
                        .timeRangeCode(UUID.randomUUID().toString())
                        .build();
                OpeningHours hours = OpeningHours.builder()
                        .dayOfWeek(day)
                        .timeRanges(Collections.singletonList(timeRange))
                        .isClosed(true)
                        .form(savedForm)
                        .build();
                openingHoursList.add(hours);
            }
            savedForm.setRegularOpeningHours(openingHoursList);
            savedForm.setSpecialDays(new ArrayList<>());
            savedForm.setHolidays(new ArrayList<>());
        }
        Form save = formRepository.save(savedForm);

        log.info("New form created {}", save);
        return FormDTO.fromEntity(save);
    }

    public List<FormDTO> retrieveFormByBranchCode(String branchCode) {
        log.info("Retrieve form by branch code [{}]", branchCode);
        return formRepository.findByBranchCodeAndFormStatusNotCancelled(branchCode)
                .map(FormDTO::fromEntityList)
                .orElseGet(Collections::emptyList);
    }

    public FormDTO retrieveFormByFormCode(String formCode) {
        log.info("Retrieve form by form code [{}]", formCode);

        return formRepository.findByFormCode(formCode)
                .map(FormDTO::fromEntity)
                .orElseGet(null);
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


    private String buildUniqueFormCode() {

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 8;
        Random random = new SecureRandom();

        StringBuilder code;
        boolean isUnique = false;

        // Keep generating a new code until we find a unique one
        do {
            code = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }

            // Check if the generated code is already in the database
            if (!formRepository.existsByFormCode(code.toString())) {
                isUnique = true;
            }
        } while (!isUnique);

        return code.toString();
    }

    @Transactional
    @Modifying
    public void switchOpeningStatus(String formCode,
                                    DayOfWeek dayOfWeek) {

        log.info("Switch opening status to {} on form with code {}", dayOfWeek, formCode);

        Form form = formRepository.findByFormCode(formCode)
                .orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        for(OpeningHours openingHours : form.getRegularOpeningHours()) {
            if(openingHours.getDayOfWeek() == dayOfWeek) {
                openingHours.setClosed(!openingHours.isClosed());
            }
        }

        formRepository.save(form);
    }

    @Transactional
    @Modifying
    public TimeRange addTimeRange(String formCode,
                                DayOfWeek dayOfWeek,
                                TimeRange timeRange) {

        log.info("Add time range {} on form with code {} for day {}", timeRange, formCode, dayOfWeek);


        Form form = formRepository.findByFormCode(formCode)
                .orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        for(OpeningHours openingHours : form.getRegularOpeningHours()) {
            if(openingHours.getDayOfWeek() == dayOfWeek) {
                timeRange.setTimeRangeCode(UUID.randomUUID().toString());
                openingHours.getTimeRanges().add(timeRange);
                formRepository.save(form);
                return timeRange;
            }
        }

        return null;
    }

    @Transactional
    @Modifying
    public FormDTO addSpecialDayConf(String formCode,
                                     TimeRange timeRange,
                                     ZonedDateTime specialDay,
                                     boolean isClosed,
                                     String descriptionSpecialDay) {

        log.info("Create a new special day configuration for form with code {} " +
                        "for the day {} - For this day is closed? [{}] and this is the time range associated {}",
                formCode,
                specialDay,
                isClosed,
                timeRange);

        Form form = formRepository.findByFormCode(formCode)
                .orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        List<TimeRange> timeRanges = new ArrayList<>();
        timeRanges.add(timeRange);

        form.getSpecialDays().add(SpecialDay.builder()
                .id(0L)
                .description(descriptionSpecialDay)
                .specialDate(specialDay)
                .timeRanges(timeRanges)
                .form(form)
                .isClosed(isClosed)
                .build());
        Form save = formRepository.save(form);
        return FormDTO.fromEntity(save);
    }

    @Transactional
    @Modifying
    public FormDTO insertHolidays(String formCode, ZonedDateTime dateFrom, ZonedDateTime dateTo, String description) {
        log.info("Insert new holidays for a form with code {} " +
                        "from day [{}] - to day [{}]. Description associated {}",
                formCode,
                dateFrom,
                dateTo,
                description);

        Form form = formRepository.findByFormCode(formCode)
                .orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));


        form.getHolidays()
                .add(Holidays
                        .builder()
                        .description(description)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .form(form)
                        .build());

        Form save = formRepository.save(form);
        return FormDTO.fromEntity(save);
    }

    @Transactional
    public void deleteOpeningHourConfById(String formCode, String timeRangeCode) {
        log.info("Delete from form with code {} the opening conf with code [{}]", formCode, timeRangeCode);
        Form form = formRepository.findByFormCode(formCode).orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        form.getRegularOpeningHours().forEach(openingHours -> {
            openingHours.getTimeRanges().removeIf(timeRange -> timeRange.getTimeRangeCode().equals(timeRangeCode));
        });

        formRepository.save(form);

    }

    @Transactional
    public void updateOpeningHourConfById(String formCode, String timeRangeCode, TimeRange timeRange) {
        log.info("Update from form with code {} the opening conf with code [{}] to this new conf [{}]", formCode, timeRangeCode, timeRange);
        Form form = formRepository.findByFormCode(formCode).orElseThrow(() -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        form.getRegularOpeningHours().forEach(openingHours -> {
            openingHours.getTimeRanges().forEach(timeRangeToUpdate -> {
                if(Objects.equals(timeRangeToUpdate.getTimeRangeCode(), timeRangeCode)){
                    timeRangeToUpdate.setOpeningHour(timeRangeToUpdate.getOpeningHour());
                    timeRangeToUpdate.setClosingHour(timeRangeToUpdate.getClosingHour());
                    timeRangeToUpdate.setOpeningMinutes(timeRangeToUpdate.getOpeningMinutes());
                    timeRangeToUpdate.setClosingMinutes(timeRangeToUpdate.getClosingMinutes());
                }
            });
        });
        formRepository.save(form);
    }

    @Transactional
    public FormDTO updateTimerange(String formCode, List<TimeRange> updatedTimeRange) {
        log.info("Update time range for form code {} the opening conf [{}]", formCode, updatedTimeRange);
        Form form = formRepository.findByFormCode(formCode).orElseThrow(()
                -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        // Iterate over the form's regular opening hours
        form.getRegularOpeningHours().forEach(openingHours -> {

            // For each existing time range, check if it matches with any incoming updated time range
            openingHours.getTimeRanges().forEach(timeRangeToUpdate -> {

                // Check for a matching timeRangeCode
                updatedTimeRange.stream()
                        .filter(updatedRange -> Objects.equals(updatedRange.getTimeRangeCode(), timeRangeToUpdate.getTimeRangeCode()))
                        .findFirst()
                        .ifPresent(updatedRange -> {
                            // Update only if the incoming value is not null
                            if (updatedRange.getOpeningHour() != null) {
                                timeRangeToUpdate.setOpeningHour(updatedRange.getOpeningHour());
                            }
                            if (updatedRange.getClosingHour() != null) {
                                timeRangeToUpdate.setClosingHour(updatedRange.getClosingHour());
                            }
                            if (updatedRange.getOpeningMinutes() != null) {
                                timeRangeToUpdate.setOpeningMinutes(updatedRange.getOpeningMinutes());
                            }
                            if (updatedRange.getClosingMinutes() != null) {
                                timeRangeToUpdate.setClosingMinutes(updatedRange.getClosingMinutes());
                            }
                        });
            });
        });

        Form save = formRepository.save(form);

        return FormDTO.fromEntity(save);
    }

    @Transactional
    public FormDTO addDefaultTimeRangeForEveryday(String formCode, TimeRange updatedTimeRange) {
        log.info("Add default time range for each days of the week for form with code {}  - New conf will be for everyday [{}]", formCode, updatedTimeRange);
        Form form = formRepository.findByFormCode(formCode).orElseThrow(()
                -> new FormNotFoundException("Exception - Form not found for code " + formCode));

        form.getRegularOpeningHours().forEach(openingHours -> {

            openingHours.getTimeRanges().add(
                    TimeRange
                            .builder()
                            .timeRangeCode(UUID.randomUUID().toString())
                            .closingHour(updatedTimeRange.getClosingHour())
                            .closingMinutes(updatedTimeRange.getClosingMinutes())
                            .openingHour(updatedTimeRange.getOpeningHour())
                            .openingMinutes(updatedTimeRange.getOpeningMinutes())
                            .build()
            );

        });

        Form save = formRepository.save(form);

        return FormDTO.fromEntity(save);
    }
}
