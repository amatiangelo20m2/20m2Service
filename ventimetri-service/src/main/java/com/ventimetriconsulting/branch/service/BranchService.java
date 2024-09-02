package com.ventimetriconsulting.branch.service;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.branch.entity.dto.CounterEntity;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.GlobalException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.notification.entity.RedirectPage;
import com.ventimetriconsulting.notification.service.MessageSender;
import com.ventimetriconsulting.order.entIty.OrderStatus;
import com.ventimetriconsulting.order.entIty.dto.OrderDTO;
import com.ventimetriconsulting.order.service.OrderService;
import com.ventimetriconsulting.storage.entity.Storage;
import com.ventimetriconsulting.storage.entity.dto.StorageDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.user.EmployeeEntity;
import com.ventimetriconsulting.user.UserResponseEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;
    private BranchUserRepository branchUserRepository;
    private MessageSender messageSender;
    private WebClient.Builder loadBalancedWebClientBuilder;
    private OrderService orderService;

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
    public List<BranchResponseEntity> getBranchesByUserCode(String userCode, String fcmToken) {


        log.info("Retrieve branches for user with code {}. If need it it will update the fcmToken to send notification, new token [{}]", userCode, fcmToken);

        List<BranchUser> branchesByUserCode = branchUserRepository
                .findBranchesByUserCode(userCode).orElseThrow(()
                        -> new BranchNotFoundException("Exception throwed while getting data for user with code : " + userCode + ". Cannot associate the storage" ));;

        if(branchesByUserCode.isEmpty()) {
            return new ArrayList<>();
        }else{
            for(BranchUser branchByUserCode : branchesByUserCode){
//                if(!Objects.equals(branchByUserCode.getFMCToken(), fcmToken)){
                branchByUserCode.setFMCToken(fcmToken);
//                }
            }

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

            List<String> fmcTokensByBranchCodeAndRole = branchUserRepository.findFMCTokensByBranchCodeAndRole(branchCode, Role.AMMINISTRATORE);


            LocalDateTime localNow = LocalDateTime.now();
            ZonedDateTime nowInGmt = localNow.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("CET"));

            fmcTokensByBranchCodeAndRole.forEach((token) -> {
                notificationEntities.add(NotificationEntity
                        .builder()
                        .title("\uD83E\uDEC2 " + userName + " vuole lavorare con te")
                        .message("L'utente " + userName + " ha richiesto di essere confermato come " + role + " per " + byBranchCode.get().getName())
                        .redirectPage(RedirectPage.EMPLOYEE)
                        .fmcToken(token)
                        .userCode(userCode)
                        .branchCode(byBranchCode.get().getBranchCode())
//                        .timeZone(nowInGmt)
//                        .isSentSuccessfully(false)
                        .build());
            });
        }

        for(NotificationEntity notificationEntity : notificationEntities){
            log.info("Sending notification to the queue. {} ", notificationEntity);
            messageSender.enqueMessage(notificationEntity);
        }

        return getBranchesByUserCode(userCode, fcmToken);
    }
    private BranchResponseEntity convertToBranchResponseEntity(BranchUser branchUser) {
//        log.info("Convert Branch User object to a dto" + branchUser.toString());
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
//        log.info("Convert Branch object to a dto" + branch.toString());
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

        List<BranchUser> branchUserByBranchCode = branchUserRepository.findBranchesByBranchCode(
                branchCode).orElseThrow(()
                -> new BranchNotFoundException("Branch user relation not found for branch with code: "
                + branchCode
                + ". Cannot retrieve employee" ));;

        for(BranchUser branchUser : branchUserByBranchCode){

            if(!Objects.equals(branchUser.getUserCode(), "0000000000")) {
                try{
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
                            .branchName(branchUser.getBranch().getName())
                            .role(branchUser.getRole())
                            .authorized(branchUser.isAuthorized())
                            .build());
                }catch (Exception e){
                    log.error(e.toString());
                }

            }
        }

        return employeeEntities;
    }

    public List<EmployeeEntity> getEmployeeByUserCode(String userCode) {

        List<EmployeeEntity> employeeEntities = new ArrayList<>();

        log.info("Retrieve all employee for user with code {}", userCode);

        Optional<List<BranchUser>> branchesByUserCode = branchUserRepository.findBranchesByUserCode(userCode);

        if(branchesByUserCode.isPresent()){
            for(BranchUser branchUser : branchesByUserCode.get()){
                List<EmployeeEntity> employeeByBranchCode = getEmployeeByBranchCode(branchUser.getBranch().getBranchCode());
                employeeEntities.addAll(employeeByBranchCode);
            }
        }else{
            log.warn("No branch found for user with code {}", userCode);
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
    public void confirmEmployee(String branchCode, String userCode, String adminUserCode) {
        log.info("Confirm employee with code {} for branch {}. Retrieve data from auth-service..", userCode, branchCode);

        BranchUser amministratore = branchUserRepository
                .findByBranchCodeAndUserCode(branchCode, adminUserCode, Role.AMMINISTRATORE).orElseThrow(()
                        -> new BranchNotFoundException("Branch user relation not found for branch with code: "
                        + branchCode + " and user code " + adminUserCode));

        UserResponseEntity userResponseEntity = loadBalancedWebClientBuilder.build()
                .get()
                .uri("http://auth-service/ventimetriauth/api/auth/retrievebyusercode",
                        uriBuilder -> uriBuilder.queryParam("userCode", amministratore.getUserCode())
                                .build())
                .retrieve()
                .bodyToMono(UserResponseEntity.class)
                .block();

        log.info("Confirm employee with code {} for branch {}", userCode, branchCode);
        Optional<BranchUser> branchesByUserCodeAndBranchCode = branchUserRepository
                .findBranchesByUserCodeAndBranchCode(userCode, branchCode);

        if(branchesByUserCodeAndBranchCode.isPresent()){

            log.info("Updating authorization for {}", branchesByUserCodeAndBranchCode.get());

            branchesByUserCodeAndBranchCode.get().setAuthorized(true);

            Collections.singletonList(branchesByUserCodeAndBranchCode.get().getFMCToken()).forEach((token) -> {
                messageSender.enqueMessage(NotificationEntity
                        .builder()
                        .fmcToken(token)
                        .branchCode(branchCode)
                        .userCode(branchesByUserCodeAndBranchCode.get().getUserCode())
                        .redirectPage(RedirectPage.DASHBOARD)
                        .branchCode(branchCode)
                        .userCode(userCode)
                        .message(Objects.requireNonNull(userResponseEntity).getName()
                                + " ha confermato il tuo ruolo come " + branchesByUserCodeAndBranchCode.get().getRole()
                                + " in " + branchesByUserCodeAndBranchCode.get().getBranch().getName() +". Aggiorna la pagina sull\'app e buon lavoro!\uD83D\uDE0E")
                        .title("\uD83E\uDD29 Ruolo confermato su " + branchesByUserCodeAndBranchCode.get().getBranch().getName())
                        .build());
            });
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

    public List<StorageDTO> retrieveStoragesByBranchCode(String branchCode) {
        log.info("Retrieve storages by branch code {}", branchCode);
        Optional<List<Storage>> storagesByBranchCode = branchRepository.findStoragesByBranchCode(branchCode);
        if(storagesByBranchCode.isPresent()){
            return StorageDTO.toDTOList(new HashSet<>(storagesByBranchCode.get()));
        }else{
            return new ArrayList<>();
        }
    }

    @Transactional
    @Modifying
    public void deleteBranch(String branchCode) {
        log.info("Delete branch with code {}", branchCode);


        Branch branch = branchRepository.findByBranchCode(branchCode).orElseThrow(()
                -> new BranchNotFoundException("No branch found with code [" + branchCode + "]. Cannot proceed deleting it."));;

        log.info("Delete all record from branch user repository where branch has id {}", branch.getBranchId());
        branchUserRepository.deleteByBranchId(branch.getBranchId());

        log.info("Delete branch with code {}", branchCode);
        branchRepository.deleteByBranchCode(branchCode);
    }

    @Transactional
    @Modifying
    public void removeUserFromBranch(String userCode, String branchCode) {
        log.info("Remove user with code {} from branch with code {}", userCode, branchCode);
        Optional<BranchUser> byUserCodeAndBranchCode = branchUserRepository.findByUserCodeAndBranchCode(userCode, branchCode);
        byUserCodeAndBranchCode.ifPresent(branchUser -> branchUserRepository.delete(branchUser));
    }

    @Transactional
    @Modifying
    public void updateEmployeeRole(String branchCode, String userCode, String role) {
        log.info("Update user with code {} from branch with code {} to role {}", userCode, branchCode, role);
        Optional<BranchUser> byUserCodeAndBranchCode = branchUserRepository.findByUserCodeAndBranchCode(userCode, branchCode);
        byUserCodeAndBranchCode.ifPresent(branchUser -> branchUser.setRole(Role.valueOf(role)));
    }

    @Transactional
    @Modifying
    public void updateBranchData(BranchCreationEntity branchCreationEntity) {
        log.info("Updating branch data {} ", branchCreationEntity);
        Branch branch = branchRepository.findByBranchCode(branchCreationEntity.getBranchCode()).orElseThrow(()
                -> new BranchNotFoundException("No branch found with code [" + branchCreationEntity.getBranchCode() + "]. Cannot proceed deleting it."));

        if (!Objects.equals(branchCreationEntity.getName(), "") && branchCreationEntity.getName() != null) {
            branch.setName(branchCreationEntity.getName());
        }

        if (!Objects.equals(branchCreationEntity.getAddress(), "") && branchCreationEntity.getAddress() != null) {
            branch.setAddress(branchCreationEntity.getAddress());
        }

        if (!Objects.equals(branchCreationEntity.getCap(), "") && branchCreationEntity.getCap() != null) {
            branch.setCap(branchCreationEntity.getCap());
        }

        if (!Objects.equals(branchCreationEntity.getCity(), "") && branchCreationEntity.getCity() != null) {
            branch.setCity(branchCreationEntity.getCity());
        }
    }

    public CounterEntity retrieveBranchCounters(String branchCode) {
        log.info("Calculate counters for branch with code {} in order to populate a mask on app mobile/ backoffice. ", branchCode);

        List<OrderDTO> ordersToConfirm = orderService.retrieveOrdersByStatus(branchCode, OrderStatus.DA_CONFERMARE);
        List<OrderDTO> ordersConsegnato = orderService.retrieveOrdersByStatus(branchCode, OrderStatus.CONSEGNATO);
        List<OrderDTO> ordersProntoAPartire = orderService.retrieveOrdersByStatus(branchCode, OrderStatus.PRONTO_A_PARTIRE);


        return CounterEntity.builder()
                .ordersCounter(CounterEntity.OrdersCounter
                        .builder()
                        .orderToConfirm(ordersToConfirm.size() + ordersConsegnato.size())
                        .orderIncoming(ordersProntoAPartire.size())
                        .build())
                .reservationCounter(CounterEntity.ReservationCounter.builder()
                        .reservationToday(999)
                        .build())
                .build();
    }
}