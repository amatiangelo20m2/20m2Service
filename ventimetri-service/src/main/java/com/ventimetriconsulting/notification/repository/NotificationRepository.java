package com.ventimetriconsulting.notification.repository;

import com.ventimetriconsulting.notification.entity.NotificationEntityTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntityTable, Long> {
    List<NotificationEntityTable> findAllByFmcTokenAfterAndInsertionDate(String fcmToken, LocalDate date);

    @Query("SELECT n FROM Notification_Entity n WHERE n.fmcToken = ?1 AND n.insertionDate >= ?2 ORDER BY n.notificationId DESC")
    List<NotificationEntityTable> findByFmcTokenAndInsertionDateBeforeOrderByNotificationIdDesc(String fmcToken, LocalDate date);

    @Modifying
    @Transactional
    @Query("UPDATE Notification_Entity n SET n.read = true WHERE n.fmcToken = ?1 AND n.read = false")
    void updateAllByFmcTokenToRead(String fcmToken);
}
