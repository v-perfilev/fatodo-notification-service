package com.persoff68.fatodo.task;

import com.persoff68.fatodo.service.NotificationService;
import com.persoff68.fatodo.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class ReminderTask {

    private final ReminderService reminderService;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void sendNotifications() {
        notificationService.sendNotifications();
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void recalculateExpiredReminders() {
        reminderService.recalculateExpiredReminders();
    }

    @Scheduled(cron = "0 * * * * *")
    public void deleteSentNotifications() {
        notificationService.deleteSentNotifications();
    }

}
