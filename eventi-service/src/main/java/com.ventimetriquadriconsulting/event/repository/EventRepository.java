package com.ventimetriquadriconsulting.event.repository;

import com.ventimetriquadriconsulting.event.entity.Event;
import com.ventimetriquadriconsulting.event.utils.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByBranchCodeAndEventStatus(String branchCode, EventStatus eventStatus);
}

