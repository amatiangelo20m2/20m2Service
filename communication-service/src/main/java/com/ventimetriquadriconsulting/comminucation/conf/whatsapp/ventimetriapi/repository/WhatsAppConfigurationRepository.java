package com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.repository;

import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.ventimetriapi.entity.WhatsAppConfiguration;
import com.ventimetriquadriconsulting.comminucation.conf.whatsapp.waapi.state_machine.entity.WaApiConfState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface WhatsAppConfigurationRepository extends JpaRepository<WhatsAppConfiguration, Long> {

    Optional<WhatsAppConfiguration> findByBranchCode(String branchCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE WhatsAppConfiguration wha SET wha.waApiInstanceId = ?1, wha.waApiConfState = ?2 WHERE wha.branchCode = ?3")
    void updateIntanceCodeToBranch(String instanceWaApiId, WaApiConfState instanceCreated, String branchCode );

    @Modifying
    @Transactional
    void deleteByBranchCode(String branchCode);
}
