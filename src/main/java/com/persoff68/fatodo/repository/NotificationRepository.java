package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, UUID> {

    @Query(value = "{ 'reminderId': ?0 }")
    List<Notification> findAllByReminderId(UUID reminderId);

    @Query(value = "{ 'status' : 'SENT' }", delete = true)
    void deleteSent();

    @Query(value = "{ 'date': {$lt: ?0}, 'status': 'CREATED' }")
    List<Notification> findAllToSend(Instant date, Pageable pageable);

}
