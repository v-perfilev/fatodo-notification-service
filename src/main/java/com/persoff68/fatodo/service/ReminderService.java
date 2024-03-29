package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.CalendarReminder;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.client.PermissionService;
import com.persoff68.fatodo.service.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private static final int WEEK_CALCULATION_PERIOD = 7;
    private static final int EXPIRED_LIMIT = 100;

    private final ReminderThreadService threadService;
    private final PermissionService permissionService;
    private final NotificationService notificationService;
    private final ReminderThreadRepository threadRepository;
    private final ReminderRepository reminderRepository;

    @Transactional
    public List<CalendarReminder> getAllCalendarRemindersByMonths(int yearFrom, int monthFrom,
                                                                  int yearTo, int monthTo,
                                                                  String timezone) {
        List<UUID> parentIdList = permissionService.getParentIds();
        List<CalendarReminder> resultList = new ArrayList<>();
        if (!parentIdList.isEmpty()) {
            Date start = DateUtils.createStartMonthsDate(yearFrom, monthFrom, timezone);
            Date end = DateUtils.createEndMonthsDate(yearTo, monthTo, timezone);
            List<Reminder> reminderList = reminderRepository.findAllByParentIds(parentIdList, end);
            List<CalendarReminder> calendarReminderList =
                    reminderList.stream()
                            .map(r -> notificationService.generatePeriodNotifications(r, timezone, start, end))
                            .flatMap(Collection::stream)
                            .filter(n -> n.getDate().compareTo(start) >= 0 && n.getDate().compareTo(end) < 0)
                            .sorted(Comparator.comparing(Notification::getDate))
                            .map(CalendarReminder::new)
                            .toList();
            resultList.addAll(calendarReminderList);
        }
        return resultList;
    }

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

        List<Reminder> reminderToDeleteList =
                existingReminderList.stream().filter(r -> !Reminder.listContains(reminderList, r)).toList();
        List<Reminder> newReminderList =
                reminderList.stream().filter(r -> !Reminder.listContains(existingReminderList, r)).toList();

        newReminderList.forEach(this::setReminderNotifications);
        newReminderList.forEach(r -> r.setThread(thread));

        existingReminderList.removeAll(reminderToDeleteList);
        existingReminderList.addAll(newReminderList);
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
        List<Notification> notificationList = notificationService
                .generateNotifications(reminder, Optional.empty(), WEEK_CALCULATION_PERIOD);
        Date lastNotificationDate = reminder.getPeriodicity().equals(Periodicity.ONCE)
                ? Date.from(ZonedDateTime.now().plusYears(100).toInstant())
                : notificationService.maxNotificationDate(notificationList);
        reminder.getNotifications().addAll(notificationList);
        reminder.setLastNotificationDate(lastNotificationDate);
    }

}
