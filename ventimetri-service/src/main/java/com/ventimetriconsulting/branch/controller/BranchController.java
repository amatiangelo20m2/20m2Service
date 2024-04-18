package com.ventimetriconsulting.branch.controller;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.branch.entity.dto.VentiMetriQuadriData;
import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.notification.entity.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.user.EmployeeEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "api/")
@AllArgsConstructor
public class BranchController {

    private BranchService branchService;

    @GetMapping(path = "/retrievedata")
    public ResponseEntity<VentiMetriQuadriData> retrieveData(@RequestParam String userCode){
        List<BranchResponseEntity> branchesByUserCode = branchService.getBranchesByUserCode(userCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(VentiMetriQuadriData
                        .builder()
                        .branches(branchesByUserCode)
                        .build());
    }

    @GetMapping(path = "/retrievebranchbyusercodeandbranchcode")
    public ResponseEntity<BranchResponseEntity> retrieveBranchByUserCodeAndBranchCode(@RequestParam String userCode, @RequestParam String branchCode){
        BranchResponseEntity branchesByUserCode = branchService.getBranchesByUserCodeAndBranchCode(userCode, branchCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(branchesByUserCode);
    }

    @GetMapping(path = "/retrieveall")
    public ResponseEntity<VentiMetriQuadriData> retrieveAll(){
        List<BranchResponseEntity> branchesByUserCode = branchService.retrieveAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(VentiMetriQuadriData
                        .builder()
                        .branches(branchesByUserCode)
                        .build());
    }

    @PutMapping(path = "/linkusertobranch")
    public ResponseEntity<VentiMetriQuadriData> linkUserToBranch(
            @RequestParam String userName,
            @RequestParam String userCode,
            @RequestParam List<String> branchCodes,
            @RequestParam Role role,
            @RequestParam String fcmToken){

        List<BranchResponseEntity> branchesByUserCode = branchService
                .linkUserToBranch(
                        userName,
                        userCode,
                        branchCodes,
                        role,
                        fcmToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(VentiMetriQuadriData
                        .builder()
                        .branches(branchesByUserCode)
                        .build());
    }

    @PostMapping(path = "/branch/save")
    public ResponseEntity<BranchResponseEntity> save(@RequestBody BranchCreationEntity branchCreationEntity) {
        BranchResponseEntity branchResponseEntity = branchService.createBranch(branchCreationEntity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(branchResponseEntity);
    }

    @GetMapping(path = "/branchdatabybranchcodeanduser")
    public ResponseEntity<BranchResponseEntity> getBranchDataByBranchCodeAndUserCode(@RequestParam String userCode,
                                                                                     @RequestParam String branchCode) {
        BranchResponseEntity branch = branchService.getBranchDataByBranchCodeAndUserCode(userCode, branchCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branch);
    }

    @GetMapping(path = "/branchdatabybranchcode")
    public ResponseEntity<BranchResponseEntity> getBranchDataByBranchCode(@RequestParam String branchCode) {
        BranchResponseEntity branchData = branchService.getBranchDataByBranchCode(branchCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branchData);
    }

    @GetMapping(path = "/branchdatabybranchtype")
    public ResponseEntity<List<BranchResponseEntity>> getBranchDataByBranchType(@RequestParam BranchType branchType) {
        List<BranchResponseEntity> branchDataByBranchType = branchService.getBranchDataByBranchType(branchType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branchDataByBranchType);
    }

    @PostMapping(path = "/setfmctoken")
    public ResponseEntity<Void> setFcmToken(@RequestParam String userCode, @RequestParam String branchCode,
                                            @RequestParam String fcmToken) {
        branchService.setFcmToken(userCode,
                branchCode,
                fcmToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(path = "/retrieveemployeebybranchcode")
    public ResponseEntity<List<EmployeeEntity>> getEmployeeByBranchCode(@RequestParam String branchCode){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branchService
                        .getEmployeeByBranchCode(branchCode));
    }

    @PutMapping(path = "/confirmemployee")
    public ResponseEntity<Void> confirmEmployee(@RequestParam String branchCode, @RequestParam String userCode){

        branchService.confirmEmployee(branchCode, userCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
