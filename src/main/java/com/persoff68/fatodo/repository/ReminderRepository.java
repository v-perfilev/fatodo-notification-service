package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Reminder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    @Query("""
            select r from Reminder r
            where r.lastNotificationDate < :date
            """)
    List<Reminder> findAllExpired(@Param("date") Date date, Pageable pageable);

}
