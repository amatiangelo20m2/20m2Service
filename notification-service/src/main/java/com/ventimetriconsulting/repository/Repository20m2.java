package com.ventimetriconsulting.repository;

import com.ventimetriconsulting.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface Repository20m2 extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userCode = :userCode AND n.timeZone BETWEEN :startDate AND :endDate order by n.timeZone DESC")
    List<NotificationEntity> findByUserCodeAndTimeZoneBetween(
            @Param("userCode") String userCode,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate
    );
}
