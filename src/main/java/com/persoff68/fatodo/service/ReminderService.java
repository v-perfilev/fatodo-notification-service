package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderThreadService threadService;
    private final NotificationService notificationService;
    private final ReminderRepository reminderRepository;

    public List<Reminder> getAllByTargetId(UUID targetId) {
        ReminderThread thread = threadService.getByTargetId(targetId);
        return reminderRepository.findAllByThreadId(thread.getId());
    }

    public void setReminders(UUID targetId, List<Reminder> reminderList) {
        ReminderThread thread = threadService.getByTargetIdOrCreate(targetId);
        List<Reminder> oldReminderList = reminderRepository.findAllByThreadId(thread.getId());
        oldReminderList.forEach(this::deleteReminder);
        reminderList.forEach(r -> addReminder(thread.getId(), r));
    }

    public void deleteReminders(UUID targetId) {
        ReminderThread thread = threadService.deleteByTargetId(targetId);
        List<Reminder> oldReminderList = reminderRepository.findAllByThreadId(thread.getId());
        oldReminderList.forEach(this::deleteReminder);
    }

    private void addReminder(UUID threadId, Reminder reminder) {
        reminder.setThreadId(threadId);
        reminder = reminderRepository.save(reminder);
        Instant lastNotificationDate = notificationService.generateNotifications(reminder);
        reminder.setLastNotificationDate(lastNotificationDate);
        reminderRepository.save(reminder);
    }

    private void deleteReminder(Reminder reminder) {
        reminderRepository.delete(reminder);
        notificationService.deleteNotifications(reminder);
    }

}
