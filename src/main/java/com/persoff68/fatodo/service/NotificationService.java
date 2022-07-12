package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.service.exception.ReminderException;
import com.persoff68.fatodo.service.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int TO_SEND_LIMIT = 100;
    private static final int WEEK_CALCULATION_PERIOD = 7;
    private static final int MONTH_CALCULATION_PERIOD = 31;

    private final MailService mailService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotifications() {
        PageRequest request = PageRequest.of(0, TO_SEND_LIMIT);
        List<Notification> notificationList = notificationRepository.findAllToSend(new Date(), request);
        setNotificationsToPending(notificationList);
        notificationList.parallelStream().forEach(mailService::sendNotification);
        setNotificationsToSent(notificationList);
    }

    @Transactional
    public void deleteSentNotifications() {
        notificationRepository.deleteSent();
    }

    public List<Notification> generateNotifications(Reminder reminder) {
        Periodicity periodicity = reminder.getPeriodicity();
        return switch (periodicity) {
            case ONCE -> createOnceNotification(reminder);
            case DAILY -> createDailyNotifications(reminder);
            case WEEKLY -> createWeeklyNotifications(reminder);
            case MONTHLY -> createMonthlyNotifications(reminder);
            case YEARLY -> createYearlyNotifications(reminder);
            default -> throw new ReminderException();
        };
    }

    public Date maxNotificationDate(List<Notification> notificationList) {
        return notificationList.stream()
                .map(Notification::getDate)
                .max(Comparator.naturalOrder())
                .orElse(createDatePlusWeek());
    }

    private List<Notification> createOnceNotification(Reminder reminder) {
        DateParams params = reminder.getDate();
        Date date = DateUtils.createDate(params);
        Notification notification = new Notification(reminder, date);
        return Collections.singletonList(notification);
    }

    private List<Notification> createDailyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        return IntStream.rangeClosed(1, WEEK_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeDate(params, i))
                .map(date -> new Notification(reminder, date))
                .toList();
    }

    private List<Notification> createWeeklyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        List<Integer> weekDays = reminder.getWeekDays();
        return IntStream.rangeClosed(1, WEEK_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeDate(params, i))
                .filter(weekDaysFilter(weekDays))
                .map(instant -> new Notification(reminder, instant))
                .toList();
    }

    private List<Notification> createMonthlyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        List<Integer> monthDays = reminder.getMonthDays();
        return IntStream.rangeClosed(1, MONTH_CALCULATION_PERIOD)
                .mapToObj(i -> DateUtils.createRelativeDate(params, i))
                .filter(monthDaysFilter(monthDays))
                .map(instant -> new Notification(reminder, instant))
                .toList();
    }

    private List<Notification> createYearlyNotifications(Reminder reminder) {
        DateParams params = reminder.getDate();
        Date date = DateUtils.createYearlyDate(params);
        Notification notification = new Notification(reminder, date);
        return Collections.singletonList(notification);
    }

    private void setNotificationsToPending(List<Notification> notificationList) {
        notificationList.forEach(n -> n.setStatus(NotificationStatus.PENDING));
        notificationRepository.saveAll(notificationList);
    }

    private void setNotificationsToSent(List<Notification> notificationList) {
        notificationList.forEach(n -> n.setStatus(NotificationStatus.SENT));
        notificationRepository.saveAll(notificationList);
    }

    private Predicate<Date> weekDaysFilter(List<Integer> weekDays) {
        return date -> {
            Instant instant = date.toInstant();
            int dayOfWeek = instant.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
            return weekDays.contains(dayOfWeek);
        };
    }

    private Predicate<Date> monthDaysFilter(List<Integer> monthDays) {
        return date -> {
            Instant instant = date.toInstant();
            int dayOfMonth = instant.atZone(ZoneId.systemDefault()).getDayOfMonth();
            return monthDays.contains(dayOfMonth);
        };
    }

    private Date createDatePlusWeek() {
        Instant instant = ZonedDateTime.now().plusWeeks(1).toInstant();
        return Date.from(instant);
    }

}
