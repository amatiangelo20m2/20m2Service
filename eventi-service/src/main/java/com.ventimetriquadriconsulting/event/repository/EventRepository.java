package com.ventimetriquadriconsulting.event.repository;

import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.branchCode = :branchCode AND e.eventStatus = :eventStatus ORDER BY e.dateEvent DESC")
    List<Event> findByBranchCodeAndEventStatusOrderByDateEventDesc(String branchCode, EventStatus eventStatus);
}

