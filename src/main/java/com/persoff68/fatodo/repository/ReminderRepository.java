package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, UUID> {

    @Query(value = "{ 'targetId': ?0 }")
    @CacheableMethod(cacheName = "reminders-by-target-id", key = "#targetId")
    List<Reminder> findAllByTargetId(UUID targetId);

    @Override
    @CacheEvictMethod(cacheName = "reminders-by-target-id", key = "#reminder.targetId")
    @NonNull
    <S extends Reminder> S save(@NonNull S reminder);

    @Override
    @CacheEvictMethod(cacheName = "reminders-by-target-id", key = "#reminder.targetId")
    void delete(@Nonnull Reminder reminder);

}
