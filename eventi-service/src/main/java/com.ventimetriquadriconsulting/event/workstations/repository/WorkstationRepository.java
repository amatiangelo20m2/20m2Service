package com.ventimetriquadriconsulting.event.workstations.repository;

import com.ventimetriquadriconsulting.event.workstations.entity.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, Long> {

}
