package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, UUID> {

    @Query(value = "{ 'reminderId': ?0 }")
    List<Notification> findAllByReminderId(UUID reminderId);

}
