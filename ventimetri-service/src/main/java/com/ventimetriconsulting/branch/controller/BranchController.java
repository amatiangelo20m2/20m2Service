package com.ventimetriconsulting.branch.controller;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.branch.entity.dto.VentiMetriQuadriData;
import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.notification.entity.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
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

    private MessageSender messageSender;

    @GetMapping(path = "/retrievedata")
    public ResponseEntity<VentiMetriQuadriData> retrieveData(@RequestParam String userCode){
        List<BranchResponseEntity> branchesByUserCode = branchService.getBranchesByUserCode(userCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(VentiMetriQuadriData
                        .builder()
                        .branches(branchesByUserCode)
                        .build());
    }

    @PutMapping(path = "/linkusertobranch")
    public ResponseEntity<VentiMetriQuadriData> linkUserToBranch(@RequestParam String userCode,
                                                                 @RequestParam List<String> branchCodes,
                                                                 @RequestParam Role role,
                                                                 @RequestParam String fcmToken){

        List<BranchResponseEntity> branchesByUserCode = branchService
                .linkUserToBranch(userCode, branchCodes, role, fcmToken);

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

    @GetMapping(path = "/publishqueue")
    public void publishMessage() {

        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("ddGCaygpRISFxQJ8omYY70:APA91bEHG8hGWW6TSnDiK7iuBCaifm6fdAVtdxQY5zbzvAOQuQ_hrjO5rozAuGPBatv3BC1PiEoSsIwyYW3glk1czdZe69787bLzgyB-dQXSq6e6vznQ73FMkG1KpfGvONT2n_SJD2UO");

        messageSender.enqueMessage(NotificationEntity
                .builder()
                .title("sadasdasdsad")
                .message("asdfsdfsdfsdfsfdsdfsdf sdfsd fsdfsdf sd f")
                .fmcToken(tokens)
                .notificationType(NotificationEntity.NotificationType.IN_APP_NOTIFICATION)
                .build());
    }
}
