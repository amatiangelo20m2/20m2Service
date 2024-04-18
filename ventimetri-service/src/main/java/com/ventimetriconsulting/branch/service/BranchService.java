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
import com.ventimetriconsulting.notification.entity.MessageSender;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.user.EmployeeEntity;
import com.ventimetriconsulting.user.UserResponseEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;

    private BranchUserRepository branchUserRepository;

    private MessageSender messageSender;

    private WebClient.Builder loadBalancedWebClientBuilder;

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
                    .role(Role.AMMINISTRATORE)
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
                    .role(Role.AMMINISTRATORE)
                    .authorized(true)
                    .logoImage(savedBranch.getLogoImage())
                    .build();

        } catch(Exception e){
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        }
    }

    public List<BranchResponseEntity> retrieveAll() {
        log.info("Retrieve all branches..");
        List<Branch> branches = branchRepository
                .findAll();
        if(branches.isEmpty()) {
            return new ArrayList<>();
        }else{
            return branches.stream()
                    .map(this::convertToBranchResponseEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<BranchResponseEntity> getBranchesByUserCode(String userCode) {

        log.info("Retrieve branches for user with code {}", userCode);
        List<BranchUser> branchesByUserCode = branchUserRepository
                .findBranchesByUserCode(userCode).orElseThrow(()
                        -> new BranchNotFoundException("Exception thowed while getting data for user with code : " + userCode + ". Cannot associate the storage" ));;

        if(branchesByUserCode.isEmpty()) {
            return new ArrayList<>();
        }else{
            return branchesByUserCode.stream()
                    .map(this::convertToBranchResponseEntity)
                    .collect(Collectors.toList());

        }
    }

    @Transactional
    public List<BranchResponseEntity> linkUserToBranch(String userName, String userCode,
                                                       List<String> branchCodes,
                                                       Role role,
                                                       String fcmToken){

        log.info("Link branches list with codes {} to the user {} with code {} " +
                "that selected role {}", branchCodes, userName, userCode, role);

        List<NotificationEntity> notificationEntities = new ArrayList<>();


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

            List<String> fmcTokensByBranchCodeAndRole
                    = branchUserRepository.findFMCTokensByBranchCodeAndRole(branchCode, Role.AMMINISTRATORE);

            notificationEntities.add(NotificationEntity
                    .builder()
                    .title("\uD83E\uDEC2 " + userName + " vuole lavorare con te")
                    .message("L'utente " + userName + " ha richiesto di essere confermato come " + role + " per " + byBranchCode.get().getName())
                    .notificationType(NotificationEntity.NotificationType.IN_APP_NOTIFICATION)
                    .fmcToken(fmcTokensByBranchCodeAndRole)
                    .build());
        }

        for(NotificationEntity notificationEntity : notificationEntities){
            log.info("Sending notification to the queue. {} ", notificationEntity);
            messageSender.enqueMessage(notificationEntity);
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

    private BranchResponseEntity convertToBranchResponseEntity(Branch branch) {
        log.info("Convert Branch object to a dto" + branch.toString());
        return BranchResponseEntity.builder()
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
                .storageDTOS(new ArrayList<>())
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

    public List<EmployeeEntity> getEmployeeByBranchCode(String branchCode) {

        List<EmployeeEntity> employeeEntities = new ArrayList<>();

        log.info("Retrieve all employee for branch with code {}", branchCode);

        List<BranchUser> branchUserByBranchCode = branchUserRepository.findBranchesEmployee(
                branchCode).orElseThrow(()
                -> new BranchNotFoundException("Branch user relation not found for branch with code: "
                + branchCode
                + ". Cannot retrieve employee" ));;

        for(BranchUser branchUser : branchUserByBranchCode){

            if(!Objects.equals(branchUser.getUserCode(), "0000000000")){
                log.info("Retrieve user details for user with code {}", branchUser.getUserCode());
                // get user data from auth-service
                UserResponseEntity userResponseEntity = loadBalancedWebClientBuilder.build()
                        .get()
                        .uri("http://auth-service/ventimetriauth/api/auth/retrievebyusercode",
                                uriBuilder -> uriBuilder.queryParam("userCode", branchUser.getUserCode())
                                        .build())
                        .retrieve()
                        .bodyToMono(UserResponseEntity.class)
                        .block();
                log.info("User details {}", userResponseEntity);
                employeeEntities.add(EmployeeEntity
                        .builder()
                        .avatar(Objects.requireNonNull(userResponseEntity).getAvatar())
                        .branchCode(branchCode)
                        .userCode(userResponseEntity.getUserCode())
                        .email(userResponseEntity.getEmail())
                        .phone(userResponseEntity.getPhone())
                        .fcmToken(branchUser.getFMCToken())
                        .name(userResponseEntity.getName())
                        .role(branchUser.getRole())
                        .authorized(branchUser.isAuthorized())
                        .build());
            }
        }

        return employeeEntities;
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
                    .storageDTOS(StorageDTO.toDTOList(byBranchCode.get().getStorages()))
                    .supplierDTOList(SupplierDTO.toDTOList(byBranchCode.get().getSuppliers()))
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

    @Transactional
    public void confirmEmployee(String branchCode, String userCode) {

        log.info("Confirm employee with code {} for branch {}", userCode, branchCode);
        Optional<BranchUser> branchesByUserCodeAndBranchCode = branchUserRepository
                .findBranchesByUserCodeAndBranchCode(userCode, branchCode);

        if(branchesByUserCodeAndBranchCode.isPresent()){
            log.info("Updating authorization for {}", branchesByUserCodeAndBranchCode.get());
            branchesByUserCodeAndBranchCode.get().setAuthorized(true);
        }
    }


    public BranchResponseEntity getBranchesByUserCodeAndBranchCode(String userCode,
                                                                         String branchCode) {

        log.info("Retrieve branch for user with code [{}] and branch code [{}]", userCode, branchCode);

        BranchUser retrievedBranch = branchUserRepository.findByUserCodeAndBranchCode(userCode, branchCode).orElseThrow(()
                -> new BranchNotFoundException("Branch user relation not found for branch with code: "
                + branchCode + " and user code " + userCode));


        return BranchResponseEntity.builder()
                .branchId(retrievedBranch.getBranch().getBranchId())
                .branchCode(retrievedBranch.getBranch().getBranchCode())
                .phone(retrievedBranch.getBranch().getPhoneNumber())
                .email(retrievedBranch.getBranch().getEmail())
                .address(retrievedBranch.getBranch().getAddress())
                .vat(retrievedBranch.getBranch().getVat())
                .type(retrievedBranch.getBranch().getType())
                .name(retrievedBranch.getBranch().getName())
                .role(retrievedBranch.getRole())
                .authorized(retrievedBranch.isAuthorized())
                .logoImage(retrievedBranch.getBranch().getLogoImage())
                .build();
    }
}
