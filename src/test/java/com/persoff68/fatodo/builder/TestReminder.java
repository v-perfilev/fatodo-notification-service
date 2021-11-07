package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TestReminder extends Reminder {

    @Builder
    TestReminder(UUID id,
                 UUID threadId,
                 Periodicity periodicity,
                 DateParams date,
                 List<Integer> weekDays,
                 List<Integer> monthDays,
                 Instant lastNotificationDate,
                 boolean locked) {
        super();
        setId(id);
        setThreadId(threadId);
        setPeriodicity(periodicity);
        setDate(date);
        setWeekDays(weekDays);
        setMonthDays(monthDays);
        setLastNotificationDate(lastNotificationDate);
        setLocked(locked);
    }

    public static TestReminderBuilder defaultBuilder() {
        DateParams date = TestDateParams.defaultBuilder().build();
        return TestReminder.builder()
                .id(UUID.randomUUID())
                .threadId(UUID.randomUUID())
                .periodicity(Periodicity.ONCE)
                .date(date)
                .lastNotificationDate(Instant.now());
    }

}
