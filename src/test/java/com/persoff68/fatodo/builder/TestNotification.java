package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public class TestNotification extends Notification {

    @Builder
    TestNotification(UUID id,
                     UUID reminderId,
                     Instant date,
                     NotificationStatus status) {
        super();
        setId(id);
        setReminderId(reminderId);
        setDate(date);
        setStatus(status);
    }

    public static TestNotificationBuilder defaultBuilder() {
        return TestNotification.builder()
                .id(UUID.randomUUID())
                .reminderId(UUID.randomUUID())
                .date(Instant.now())
                .status(NotificationStatus.CREATED);
    }

}
