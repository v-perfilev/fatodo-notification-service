package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.Builder;

import java.util.Date;
import java.util.List;

public class TestReminder extends Reminder {

    @Builder
    TestReminder(ReminderThread thread,
                 Periodicity periodicity,
                 DateParams date,
                 List<Integer> weekDays,
                 List<Integer> monthDays,
                 Date lastNotificationDate) {
        super();
        setThread(thread);
        setPeriodicity(periodicity);
        setDate(date);
        setWeekDays(weekDays);
        setMonthDays(monthDays);
        setLastNotificationDate(lastNotificationDate);
    }

    public static TestReminderBuilder defaultBuilder() {
        DateParams date = TestDateParams.defaultBuilder().build().toParent();
        return TestReminder.builder()
                .periodicity(Periodicity.ONCE)
                .date(date)
                .lastNotificationDate(new Date());
    }

    public Reminder toParent() {
        Reminder reminder = new Reminder();
        reminder.setThread(getThread());
        reminder.setPeriodicity(getPeriodicity());
        reminder.setDate(getDate());
        reminder.setWeekDays(getWeekDays());
        reminder.setMonthDays(getMonthDays());
        reminder.setLastNotificationDate(getLastNotificationDate());
        return reminder;
    }

}
