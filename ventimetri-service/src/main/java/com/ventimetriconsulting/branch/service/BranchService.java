package com.ventimetriconsulting.branch.service;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.*;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.branch.exception.customexceptions.GlobalException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.inventario.entity.dto.StorageDTO;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;

    private BranchUserRepository branchUserRepository;

    @Transactional
    public BranchResponseEntity createBranch(BranchCreationEntity branchCreationEntity) {

        try{
            log.info("Branch Service - Create branch by user [{}] - Branch Type {} - Branch Entity {}",
                    branchCreationEntity.getUserCode(),
                    branchCreationEntity.getType(),
                    branchCreationEntity);


            Branch savedBranch = branchRepository.save(
                    Branch.builder()
                            .branchId(0)
                            .phoneNumber(branchCreationEntity.getPhoneNumber())
                            .vat(branchCreationEntity.getVat())
                            .name(branchCreationEntity.getName())
                            .address(branchCreationEntity.getAddress())
                            .email(branchCreationEntity.getEmail())
                            .city(branchCreationEntity.getCity())
                            .cap(branchCreationEntity.getCap())
                            .type(branchCreationEntity.getType())
                            .logoImage(branchCreationEntity.getLogoImage())
                            .build());

            log.info("Link branch created with id {} to a user with mail {}", savedBranch.getBranchId(), branchCreationEntity.getEmail());
            branchUserRepository.save(BranchUser.builder()
                    .id(0)
                    .branch(savedBranch)
                    .userCode(branchCreationEntity.getUserCode())
                    .role(Role.PROPRIETARIO)
                    .authorized(true)
                    .fMCToken(branchCreationEntity.getFcmToken())
                    .build());


            return BranchResponseEntity.builder()
                    .branchId(savedBranch.getBranchId())
                    .branchCode(savedBranch.getBranchCode())
                    .phone(savedBranch.getPhoneNumber())
                    .email(savedBranch.getEmail())
                    .address(savedBranch.getAddress())
                    .vat(savedBranch.getVat())
                    .type(savedBranch.getType())
                    .name(savedBranch.getName())
                    .role(Role.PROPRIETARIO)
                    .authorized(true)
                    .logoImage(savedBranch.getLogoImage())
                    .build();

        } catch(Exception e){
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        }
    }

    @Transactional
    public List<BranchResponseEntity> getBranchesByUserCode(String userCode) {

        log.info("Retrieve branches for user with code {}", userCode);
        List<BranchUser> branchesByUserCode = branchUserRepository
                .findBranchesByUserCode(userCode).orElseThrow(() -> new BranchNotFoundException("Exception thowed while getting data for user with code : " + userCode + ". Cannot associate the storage" ));;

        if(branchesByUserCode.isEmpty()) {
            return new ArrayList<>();
        }else{
            return branchesByUserCode.stream()
                    .map(this::convertToBranchResponseEntity)
                    .collect(Collectors.toList());

        }
    }

    @Transactional
    public List<BranchResponseEntity> linkUserToBranch(String userCode,
                                                       List<String> branchCodes,
                                                       Role role,
                                                       String fcmToken){

        log.info("Link branches list with codes {} to the user with code {} that selected role {}", branchCodes, userCode, role);

        for(String branchCode : branchCodes){
            Optional<Branch> byBranchCode = branchRepository.findByBranchCode(branchCode);
            byBranchCode.ifPresent(branch -> branchUserRepository.save(BranchUser.builder()
                    .id(0)
                    .branch(branch)
                    .userCode(userCode)
                    .authorized(false)
                    .role(role)
                    .fMCToken(fcmToken)
                    .build()));
        }

        return getBranchesByUserCode(userCode);
    }
    private BranchResponseEntity convertToBranchResponseEntity(BranchUser branchUser) {
        log.info("Convert Branch User object to a dto" + branchUser.toString());
        return BranchResponseEntity.builder()
                .branchId(branchUser.getBranch().getBranchId())
                .name(branchUser.getBranch().getName())
                .address(branchUser.getBranch().getAddress())
                .email(branchUser.getBranch().getEmail())
                .phone(branchUser.getBranch().getPhoneNumber())
                .vat(branchUser.getBranch().getVat())
                .type(branchUser.getBranch().getType())
                .branchCode(branchUser.getBranch().getBranchCode())
                .logoImage(branchUser.getBranch().getLogoImage())
                .role(branchUser.getRole())
                .authorized(branchUser.isAuthorized())
                .supplierDTOList(SupplierDTO.toDTOList(branchUser.getBranch().getSuppliers()))
                .storageDTOS(StorageDTO.toDTOList(branchUser.getBranch().getStorages()))
                .build();
    }

    public BranchResponseEntity getBranchDataByBranchCodeAndUserCode(String userCode,
                                                                     String branchCode) {

        log.info("Retrieve branch for user with code {} and branch with code {}", userCode, branchCode);
        Optional<BranchUser> branchByUserCodeAndBranchCode = branchUserRepository.findBranchesByUserCodeAndBranchCode(userCode, branchCode);

        if(branchByUserCodeAndBranchCode.isPresent()) {
            return convertToBranchResponseEntity(branchByUserCodeAndBranchCode.get());
        }
        throw new BranchNotFoundException("Branch not found for user with code [" + userCode + "] and branch with code [" + branchCode + "] ");
    }

    public BranchResponseEntity getBranchDataByBranchCode(String branchCode) {
        log.info("Retrieve branch info by code {}", branchCode);
        Optional<Branch> byBranchCode = branchRepository.findByBranchCode(branchCode);
        if(byBranchCode.isPresent()){
            return BranchResponseEntity.builder()
                    .branchId(byBranchCode.get().getBranchId())
                    .name(byBranchCode.get().getName())
                    .branchCode(byBranchCode.get().getBranchCode())
                    .address(byBranchCode.get().getAddress())
                    .logoImage(byBranchCode.get().getLogoImage())
                    .phone(byBranchCode.get().getPhoneNumber())
                    .email(byBranchCode.get().getEmail())
                    .authorized(true)
                    .build();
        }else{
            log.error("GetBranchData method give error. No branch found with branch code " + branchCode);
            throw new BranchNotFoundException("No branch found with branch code " + branchCode);
        }
    }

    @Transactional
    public void setFcmToken(String userCode,
                            String branchCode,
                            String fcmToken) {
        log.info("Configure fcm token for branch code {}. User Code {}. FCM Token: {}", branchCode, userCode, fcmToken);

        Optional<List<BranchUser>> branchesByUserCode = branchUserRepository.findBranchesByUserCode(userCode);

        if(branchesByUserCode.isPresent()){
            if(!branchesByUserCode.get().isEmpty()) {
                for(BranchUser branchUser : branchesByUserCode.get()){
                    branchUser.setFMCToken(fcmToken);
                }
            }
        }
    }


    public List<BranchResponseEntity> getBranchDataByBranchType(BranchType branchType) {

        log.info("Get branch list by type {}", branchType);
        List<BranchResponseEntity> branchResponseEntities = new ArrayList<>();

        List<Branch> branchByTypeList = branchRepository.findByType(branchType);

        for(Branch branch : branchByTypeList) {

            branchResponseEntities.add(BranchResponseEntity.builder()
                            .branchId(branch.getBranchId())
                            .name(branch.getName())
                            .address(branch.getAddress())
                            .email(branch.getEmail())
                            .phone(branch.getPhoneNumber())
                            .vat(branch.getVat())
                            .type(branch.getType())
                            .branchCode(branch.getBranchCode())
                            .logoImage(branch.getLogoImage())
                            .role(null)
                            .authorized(false)
                            .supplierDTOList(new ArrayList<>())
                            .storageDTOS(StorageDTO.toDTOList(branch.getStorages()))
                            .build());
        }
        return branchResponseEntities;
    }
}
