package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.Date;

public class TestNotification extends Notification {

    @Builder
    TestNotification(Reminder reminder,
                     Date date,
                     NotificationStatus status) {
        super();
        setReminder(reminder);
        setDate(date);
        setStatus(status);
    }

    public static TestNotificationBuilder defaultBuilder() {
        return TestNotification.builder()
                .date(new Date())
                .status(NotificationStatus.CREATED);
    }

    public Notification toParent() {
        Notification notification = new Notification();
        notification.setReminder(getReminder());
        notification.setDate(getDate());
        notification.setStatus(getStatus());
        return notification;
    }

}
