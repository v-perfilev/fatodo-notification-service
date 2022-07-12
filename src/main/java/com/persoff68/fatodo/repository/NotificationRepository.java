package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Modifying
    @Query("""
            delete from Notification n
            where n.status = com.persoff68.fatodo.model.constant.NotificationStatus.SENT
            """)
    void deleteSent();

    @Query("""
            select n from Notification n
            where n.date < :date and n.status = com.persoff68.fatodo.model.constant.NotificationStatus.CREATED
            """)
    List<Notification> findAllToSend(@Param("date") Date date, Pageable pageable);

}
