package com.ventimetriquadriconsulting.restaurant.restaurant.form.controller;


import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.dto.FormDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.form.service.FormService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
