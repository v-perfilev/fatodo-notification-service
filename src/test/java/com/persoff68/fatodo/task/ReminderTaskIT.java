package com.persoff68.fatodo.task;

import com.persoff68.fatodo.FatodoNotificationServiceApplication;
import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderInfo;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.builder.TestUserInfo;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ItemSystemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.UserInfo;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FatodoNotificationServiceApplication.class)
class ReminderTaskIT {

    @Autowired
    ReminderTask reminderTask;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @MockBean
    ItemSystemServiceClient itemSystemServiceClient;
    @MockBean
    MailServiceClient mailServiceClient;
    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;

    ReminderThread thread;

    @BeforeEach
    void setup() {
        thread = TestReminderThread.defaultBuilder().build().toParent();
        thread = threadRepository.save(thread);

        ReminderInfo message = TestReminderInfo.defaultBuilder().build().toParent();
        when(itemSystemServiceClient.getReminderMailInfo(any())).thenReturn(message);
        UserInfo userInfo = TestUserInfo.defaultBuilder().build().toParent();
        when(userServiceClient.getAllInfoByIds(any())).thenReturn(Collections.singletonList(userInfo));
    }

    @AfterEach
    void cleanup() {
        threadRepository.deleteAll();
    }

    @Test
    void testSendNotifications() {
        Date date = Date.from(Instant.now().minusSeconds(10));
        Reminder reminder = TestReminder.defaultBuilder().thread(thread).lastNotificationDate(date).build().toParent();
        Notification notification = TestNotification.defaultBuilder().reminder(reminder).date(date).build().toParent();
        reminder.setNotifications(List.of(notification));
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        reminderTask.sendNotifications();

        verify(mailServiceClient, timeout(1000)).sendNotification(any());
        verify(eventServiceClient, timeout(1000)).addEvent(any());
        verify(wsServiceClient, timeout(1000)).sendEvent(any());

        List<Notification> notificationList = notificationRepository.findAll();
        Condition<Notification> sentCondition = new Condition<>(
                n -> n.getStatus().equals(NotificationStatus.SENT),
                "notification is sent"
        );
        assertThat(notificationList).have(sentCondition);
    }

    @Test
    void testRecalculateExpiredReminders() {
        Date date = Date.from(Instant.now().minusSeconds(10));
        Reminder reminder = TestReminder.defaultBuilder().thread(thread)
                .periodicity(Periodicity.DAILY).lastNotificationDate(date).build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        reminderTask.recalculateExpiredReminders();

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).isNotEmpty();
    }

    @Test
    void testDeleteSentNotifications() {
        Date date = Date.from(Instant.now().minusMillis(1000));
        Reminder reminder = TestReminder.defaultBuilder()
                .thread(thread).periodicity(Periodicity.DAILY).lastNotificationDate(date).build().toParent();
        Notification notification = TestNotification.defaultBuilder()
                .reminder(reminder).status(NotificationStatus.SENT).build().toParent();
        reminder.setNotifications(List.of(notification));
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        reminderTask.deleteSentNotifications();

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).isEmpty();
    }

}


