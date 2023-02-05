package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ItemSystemServiceClient;
import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.MailService;
import com.persoff68.fatodo.service.client.WsService;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int TO_SEND_LIMIT = 100;

    private final ItemSystemServiceClient itemSystemServiceClient;

    private final NotificationRepository notificationRepository;
    private final EventService eventService;
    private final MailService mailService;
    private final WsService wsService;

    @Transactional
    public void sendNotifications() {
        PageRequest request = PageRequest.of(0, TO_SEND_LIMIT);
        List<Notification> notificationList = notificationRepository.findAllToSend(new Date(), request);
        setNotificationsToPending(notificationList);
        notificationList.parallelStream().forEach(notification -> {
            Reminder reminder = notification.getReminder();
            ReminderInfo reminderInfo = getReminderInfoByThread(reminder.getThread());

            // MAIL
            mailService.sendNotification(reminderInfo);
            // EVENT
            eventService.sendReminderEvent(reminderInfo);
            // WS
            wsService.sendReminderEvent(reminderInfo);
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
        Instant startInstant = startCalendar.toInstant().truncatedTo(ChronoUnit.SECONDS);
        Calendar endCalendar = DateUtils.createCalendar(timezone, end);
        Instant endInstant = endCalendar.toInstant().truncatedTo(ChronoUnit.SECONDS);
        // add one day if createdAt is later than 00:00, will be filtered ReminderService
        int periodInDays = (int) ChronoUnit.DAYS.between(startInstant, endInstant) + 1;
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
            case YEARLY -> createYearlyNotifications(reminder, startOptional, periodInDays);
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

    private List<Notification> createYearlyNotifications(Reminder reminder,
                                                         Optional<Calendar> startOptional,
                                                         int calculationPeriod) {
        DateParams params = reminder.getDate();
        Date relativeStartDate = DateUtils.createRelativeDate(params, startOptional, 0);
        Date startDate = Optional.ofNullable(relativeStartDate)
                .orElse(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
        Date endDate = DateUtils.createRelativeDate(params, startOptional, calculationPeriod);

        return Stream.of(startDate, endDate)
                .filter(Objects::nonNull)
                .map(date -> {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    return calendar.get(Calendar.YEAR);
                })
                .distinct()
                .map(year -> DateUtils.createYearlyDate(params, year))
                .filter(date -> date.after(startDate))
                .filter(date -> date.before(endDate))
                .map(date -> new Notification(reminder, date))
                .toList();
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
            int dayOfWeek = instant.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue() - 1;
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

    private ReminderInfo getReminderInfoByThread(ReminderThread thread) {
        UUID targetId = thread.getTargetId();
        ReminderThreadType type = thread.getType();
        return switch (type) {
            case ITEM -> itemSystemServiceClient.getReminderMailInfo(targetId);
        };
    }

}
