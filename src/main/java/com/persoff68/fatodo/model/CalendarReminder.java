package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarReminder {

    private Reminder reminder;

    private ReminderThread thread;

    private Date date;

    public CalendarReminder(Notification notification) {
        this.reminder = notification.getReminder();
        this.thread = notification.getReminder().getThread();
        this.date = notification.getDate();
    }

}
