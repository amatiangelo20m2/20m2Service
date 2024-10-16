package com.ventimetriquadriconsulting.restaurant.restaurant.form.controller;


import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.TimeRange;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto.FormDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.service.FormService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/form")
@AllArgsConstructor
public class FormController {


    private FormService formService;

    @PostMapping(path = "/create")
    public ResponseEntity<FormDTO> createForm(@RequestBody FormDTO formDTO) {
        return ResponseEntity.ok(formService.createForm(formDTO));
    }

    @GetMapping(path = "/retrieveall")
    public ResponseEntity<List<FormDTO>> retrieveAll(){
        return ResponseEntity.ok(formService.retrieveAll());
    }

    @GetMapping(path = "/retrievebybranchcode/{branchCode}")
    public ResponseEntity<List<FormDTO>> retrieveByBranchCode(@PathVariable String branchCode){
        return ResponseEntity.ok(formService.retrieveFormByBranchCode(branchCode));
    }

    @GetMapping(path = "/retrievebyformcode/{formCode}")
    public ResponseEntity<FormDTO> retrieveByFormCode(@PathVariable String formCode){
        return ResponseEntity.ok(formService.retrieveFormByFormCode(formCode));
    }

    @PutMapping(path = "/editform")
    public ResponseEntity<FormDTO> editForm(@RequestBody FormDTO formDTO){
        return ResponseEntity.ok(formService.editForm(formDTO));
    }


    @PutMapping(path = "/switchOpeningStatus/{formCode}/{dayOfWeek}")
    public ResponseEntity<Void> switchOpeningStatus(@PathVariable String formCode,
                                                    @PathVariable DayOfWeek dayOfWeek) {

        formService.switchOpeningStatus(formCode, dayOfWeek);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/add/timerange/{formCode}/{dayOfWeek}")
    public ResponseEntity<TimeRange> addTimeRange(
            @PathVariable String formCode,
            @PathVariable DayOfWeek dayOfWeek,
            @RequestBody TimeRange timeRange) {

        return ResponseEntity.ok(formService.addTimeRange(formCode, dayOfWeek, timeRange));

    }

    @PutMapping(path = "/add/specialdayconf/{formCode}")
    public ResponseEntity<FormDTO> addSpecialDayConf(
            @PathVariable String formCode,
            @RequestBody TimeRange timeRange,
            @RequestBody ZonedDateTime specialDay,
            @RequestParam boolean isClosed,
            @RequestParam String descriptionSpecialDay) {

        return ResponseEntity.ok(formService.addSpecialDayConf(formCode, timeRange, specialDay, isClosed, descriptionSpecialDay));

    }

    @PutMapping(path = "/add/holidays/{formCode}")
    public ResponseEntity<FormDTO> insertHolidays(@PathVariable String formCode,
                                                  @RequestParam ZonedDateTime dateFrom,
                                                  @RequestParam ZonedDateTime dateTo,
                                                  @RequestParam String description) {

        return ResponseEntity.ok(formService.insertHolidays(formCode, dateFrom, dateTo, description));

    }


    @DeleteMapping(path = "/delete/openinghourconf/{formCode}/{timeRangeCode}")
    public ResponseEntity<Void> deleteOpeningHourConfByCode(@PathVariable String formCode,
                                                            @PathVariable String timeRangeCode) {

        formService.deleteOpeningHourConfById(formCode, timeRangeCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/update/openinghourconf/{formCode}/{timeRangeCode}")
    public ResponseEntity<Void> updateOpeningHourConfByCode(@PathVariable String formCode,
                                                            @PathVariable String timeRangeCode,
                                                            @RequestParam TimeRange timeRange) {

        formService.updateOpeningHourConfById(formCode, timeRangeCode, timeRange);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/update/timerange/{formCode}")
    public ResponseEntity<FormDTO> updateTimeRange(@PathVariable String formCode,
                                                   @RequestBody List<TimeRange> updatedTimeRange) {

        return ResponseEntity
                .ok(formService
                        .updateTimerange(
                                formCode,
                                updatedTimeRange));
    }

    @PutMapping(path = "/create/defaulttimerange/{formCode}")
    public ResponseEntity<FormDTO> addDefaultTimeRangeForEveryday(@PathVariable String formCode,
                                                                  @RequestBody TimeRange updatedTimeRange) {

        return ResponseEntity
                .ok(formService
                        .addDefaultTimeRangeForEveryday(
                                formCode,
                                updatedTimeRange));

    }
}
