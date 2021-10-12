package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.ReminderThread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReminderThreadRepository extends MongoRepository<ReminderThread, UUID> {

    @Query(value = "{ 'targetId': ?0 }")
    @CacheableMethod(cacheName = "thread-by-target-id", key = "#targetId")
    Optional<ReminderThread> findByTargetId(UUID targetId);

    @CacheEvictMethod(cacheName = "thread-by-target-id", key = "#targetId")
    void deleteByTargetId(@Nonnull UUID targetId);

}
