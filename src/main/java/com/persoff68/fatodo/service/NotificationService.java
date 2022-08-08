package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.MailService;
import com.persoff68.fatodo.service.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int TO_SEND_LIMIT = 100;

    private final NotificationRepository notificationRepository;
    private final EventService eventService;
    private final MailService mailService;

    @Transactional
    public void sendNotifications() {
        PageRequest request = PageRequest.of(0, TO_SEND_LIMIT);
        List<Notification> notificationList = notificationRepository.findAllToSend(new Date(), request);
        setNotificationsToPending(notificationList);
        notificationList.parallelStream().forEach(notification -> {
            Reminder reminder = notification.getReminder();
            eventService.sendReminderEvent(reminder);
            mailService.sendNotification(notification);
        });
        setNotificationsToSent(notificationList);
    }

    @Transactional
    public void deleteSentNotifications() {
        notificationRepository.deleteSent();
    }

    public List<Notification> generatePeriodNotifications(Reminder reminder, String timezone, Date start, Date end) {
        Date createdAt = reminder.getCreatedAt();
        Date relativeDate = createdAt.compareTo(start) > 0 ? createdAt : start;
        Calendar startCalendar = DateUtils.createCalendar(timezone, relativeDate);
        Calendar endCalendar = DateUtils.createCalendar(timezone, end);
        int periodInDays = (int) ChronoUnit.DAYS.between(startCalendar.toInstant(), endCalendar.toInstant());
        return generateNotifications(reminder, Optional.of(startCalendar), periodInDays);
    }

    public List<Notification> generateNotifications(Reminder reminder,
                                                    Optional<Calendar> startOptional,
                                                    int periodInDays) {
        Periodicity periodicity = reminder.getPeriodicity();
        return switch (periodicity) {
            case ONCE -> createOnceNotification(reminder);
            case DAILY -> createDailyNotifications(reminder, startOptional, periodInDays);
            case WEEKLY -> createWeeklyNotifications(reminder, startOptional, periodInDays);
            case MONTHLY -> createMonthlyNotifications(reminder, startOptional, periodInDays);
            case YEARLY -> createYearlyNotifications(reminder);
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

    private List<Notification> createDailyNotifications(Reminder reminder,
                                                        Optional<Calendar> startOptional,
                                                        int calculationPeriod) {
        DateParams params = reminder.getDate();
        return IntStream.rangeClosed(0, calculationPeriod)
                .mapToObj(i -> DateUtils.createRelativeDate(params, startOptional, i))
                .filter(Objects::nonNull)
                .map(date -> new Notification(reminder, date))
                .toList();
    }

    private List<Notification> createWeeklyNotifications(Reminder reminder,
                                                         Optional<Calendar> startOptional,
                                                         int calculationPeriod) {
        DateParams params = reminder.getDate();
        List<Integer> weekDays = reminder.getWeekDays();
        return IntStream.rangeClosed(0, calculationPeriod)
                .mapToObj(i -> DateUtils.createRelativeDate(params, startOptional, i))
                .filter(Objects::nonNull)
                .filter(weekDaysFilter(weekDays))
                .map(instant -> new Notification(reminder, instant))
                .toList();
    }

    private List<Notification> createMonthlyNotifications(Reminder reminder,
                                                          Optional<Calendar> startOptional,
                                                          int calculationPeriod) {
        DateParams params = reminder.getDate();
        List<Integer> monthDays = reminder.getMonthDays();
        return IntStream.rangeClosed(0, calculationPeriod)
                .mapToObj(i -> DateUtils.createRelativeDate(params, startOptional, i))
                .filter(Objects::nonNull)
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
