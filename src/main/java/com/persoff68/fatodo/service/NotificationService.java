package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.service.exception.ReminderException;
import com.persoff68.fatodo.service.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int WEEK_CALCULATION_PERIOD = 7;
    private static final int MONTH_CALCULATION_PERIOD = 31;

    private final NotificationRepository notificationRepository;

    public Instant generateNotifications(Reminder reminder) {
        Periodicity periodicity = reminder.getPeriodicity();
        List<Notification> notificationList = switch (periodicity) {
            case ONCE -> createOnceNotification(reminder);
            case DAILY -> createDailyNotifications(reminder);
            case WEEKLY -> createWeeklyNotifications(reminder);
            case MONTHLY -> createMonthlyNotifications(reminder);
            case YEARLY -> createYearlyNotifications(reminder);
        };
        notificationRepository.saveAll(notificationList);
        return periodicity.equals(Periodicity.ONCE)
                ? Instant.MAX
                : maxNotificationDate(notificationList);
    }

    public void deleteNotifications(Reminder reminder) {
        List<Notification> notificationList = notificationRepository.findAllByReminderId(reminder.getId());
        notificationRepository.deleteAll(notificationList);
    }

    private List<Notification> createOnceNotification(Reminder reminder) {
        DateParams params = reminder.getDate();
        Instant instant = DateUtils.createInstant(params);
        Notification notification = new Notification(reminder.getId(), instant);
        return Collections.singletonList(notification);
    }

    private List<Notification> createDailyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        return IntStream.rangeClosed(1, WEEK_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeInstant(params, i))
                .map(instant -> new Notification(reminder.getId(), instant))
                .collect(Collectors.toList());
    }

    private List<Notification> createWeeklyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        List<Integer> weekDays = reminder.getWeekDays();
        return IntStream.rangeClosed(1, WEEK_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeInstant(params, i))
                .filter(weekDaysFilter(weekDays))
                .map(instant -> new Notification(reminder.getId(), instant))
                .collect(Collectors.toList());
    }

    private List<Notification> createMonthlyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        List<Integer> monthDays = reminder.getMonthDays();
        return IntStream.rangeClosed(1, MONTH_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeInstant(params, i))
                .filter(monthDaysFilter(monthDays))
                .map(instant -> new Notification(reminder.getId(), instant))
                .collect(Collectors.toList());
    }

    private List<Notification> createYearlyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        Instant instant = DateUtils.createYearlyInstant(params);
        Notification notification = new Notification(reminder.getId(), instant);
        return Collections.singletonList(notification);
    }

    private Predicate<Instant> weekDaysFilter(List<Integer> weekDays) {
        return instant -> {
            int dayOfWeek = instant.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
            return weekDays.contains(dayOfWeek);
        };
    }

    private Predicate<Instant> monthDaysFilter(List<Integer> monthDays) {
        return instant -> {
            int dayOfMonth = instant.atZone(ZoneId.systemDefault()).getDayOfMonth();
            return monthDays.contains(dayOfMonth);
        };
    }

    private Instant maxNotificationDate(List<Notification> notificationList) {
        return notificationList.stream()
                .map(Notification::getDate)
                .max(Comparator.naturalOrder())
                .orElseThrow(ReminderException::new);
    }

}
