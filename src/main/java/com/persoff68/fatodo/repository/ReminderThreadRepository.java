package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.ReminderThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReminderThreadRepository extends JpaRepository<ReminderThread, UUID> {

    List<ReminderThread> findAllByParentId(UUID targetId);

    Optional<ReminderThread> findByTargetId(UUID targetId);

}
