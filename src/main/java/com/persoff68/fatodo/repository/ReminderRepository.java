package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, UUID> {

    @Query(value = "{ 'threadId': ?0 }")
    @CacheableMethod(cacheName = "reminders-by-thread-id", key = "#threadId")
    List<Reminder> findAllByThreadId(UUID threadId);

    @Override
    @CacheEvictMethod(cacheName = "reminders-by-thread-id", key = "#reminder.threadId")
    @NonNull
    <S extends Reminder> S save(@NonNull S reminder);

    @Override
    @CacheEvictMethod(cacheName = "reminders-by-thread-id", key = "#reminder.threadId")
    void delete(@Nonnull Reminder reminder);

    //    @Query(value = "{ 'lastNotificationDate': {$lt: ?0}, 'locked': 'false' }, $limit: $1")
//    @Query(value = "{ 'locked': 'false' }")
//    @Query(value = "{ 'lastNotificationDate': {$lt: ?0}, 'locked': 'false' }, $limit: $1")
    @Query(value = "{ 'lastNotificationDate': {$lt: ?0}, 'locked': false }, $limit: $1")
    List<Reminder> findAllExpired(Instant date, int limit);

}
