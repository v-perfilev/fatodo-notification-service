package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private static final int EXPIRED_LIMIT = 100;

    private final ReminderThreadService threadService;
    private final ReminderThreadRepository threadRepository;
    private final NotificationService notificationService;
    private final ReminderRepository reminderRepository;

    @Transactional
    public List<Reminder> getAllByTargetId(UUID targetId) {
        ReminderThread thread = threadService.getByTargetId(targetId);
        Hibernate.initialize(thread.getReminders());
        return thread.getReminders();
    }

    @Transactional
    public void setReminders(UUID targetId, List<Reminder> reminderList) {
        ReminderThread thread = threadService.getByTargetIdOrCreate(targetId);
        List<Reminder> existingReminderList = thread.getReminders();

        List<Reminder> reminderToDeleteList = existingReminderList.stream()
                .filter(r -> !reminderList.contains(r)).toList();
        List<Reminder> newReminderList = reminderList.stream()
                .filter(r -> !existingReminderList.contains(r)).toList();

        newReminderList.forEach(this::setReminderNotifications);
        newReminderList.forEach(r -> r.setThread(thread));

        thread.getReminders().removeAll(reminderToDeleteList);
        thread.getReminders().addAll(newReminderList);
        threadRepository.save(thread);
    }

    @Transactional
    public void recalculateExpiredReminders() {
        PageRequest request = PageRequest.of(0, EXPIRED_LIMIT);
        List<Reminder> reminderList = reminderRepository.findAllExpired(new Date(), request);
        reminderList.forEach(this::setReminderNotifications);
        reminderRepository.saveAll(reminderList);
    }

    private void setReminderNotifications(Reminder reminder) {
        List<Notification> notificationList = notificationService.generateNotifications(reminder);
        Date lastNotificationDate = reminder.getPeriodicity().equals(Periodicity.ONCE)
                ? Date.from(ZonedDateTime.now().plusYears(100).toInstant())
                : notificationService.maxNotificationDate(notificationList);
        reminder.getNotifications().addAll(notificationList);
        reminder.setLastNotificationDate(lastNotificationDate);
    }

}
