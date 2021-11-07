package com.persoff68.fatodo.task;

import com.persoff68.fatodo.FatodoNotificationServiceApplication;
import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderMessage;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.builder.TestUserInfo;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderMessage;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.UserInfo;
import com.persoff68.fatodo.model.constant.NotificationStatus;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderRepository;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FatodoNotificationServiceApplication.class)
public class ReminderTaskIT {

    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final UUID THREAD_ID = UUID.randomUUID();
    private static final UUID REMINDER_ID = UUID.randomUUID();

    @Autowired
    ReminderTask reminderTask;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    ReminderRepository reminderRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @MockBean
    ItemServiceClient itemServiceClient;
    @MockBean
    UserServiceClient userServiceClient;
    @MockBean
    MailServiceClient mailServiceClient;

    @BeforeEach
    public void setup() {
        ReminderThread thread = TestReminderThread.defaultBuilder().id(THREAD_ID).targetId(TARGET_ID).build();
        threadRepository.save(thread);

        ReminderMessage message = TestReminderMessage.defaultBuilder().build();
        when(itemServiceClient.getReminderByItemId(any())).thenReturn(message);
        UserInfo userInfo = TestUserInfo.defaultBuilder().build();
        when(userServiceClient.getAllInfoByIds(any())).thenReturn(Collections.singletonList(userInfo));
    }

    @AfterEach
    public void cleanup() {
        notificationRepository.deleteAll();
        reminderRepository.deleteAll();
        threadRepository.deleteAll();
    }

    @Test
    public void testSendNotifications() {

        Instant instant = Instant.now().minusSeconds(10);
        Reminder reminder = TestReminder.defaultBuilder()
                .id(REMINDER_ID).threadId(THREAD_ID).lastNotificationDate(instant).build();
        reminderRepository.save(reminder);
        Notification notification = TestNotification.defaultBuilder()
                .reminderId(REMINDER_ID).date(instant).build();
        notificationRepository.save(notification);

        reminderTask.sendNotifications();

        verify(mailServiceClient).sendNotification(any());
        List<Notification> notificationList = notificationRepository.findAll();
        Condition<Notification> sentCondition = new Condition<>(
                n -> n.getStatus().equals(NotificationStatus.SENT),
                "notification is sent"
        );
        assertThat(notificationList).have(sentCondition);
    }

    @Test
    public void testRecalculateExpiredReminders() {
        Instant instant = Instant.now().minusSeconds(10);
        Reminder reminder = TestReminder.defaultBuilder()
                .id(REMINDER_ID).threadId(THREAD_ID)
                .periodicity(Periodicity.DAILY).lastNotificationDate(instant).build();
        reminderRepository.save(reminder);

        reminderTask.recalculateExpiredReminders();

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).isNotEmpty();
    }

    @Test
    public void testDeleteSentNotifications() {
        Instant instant = Instant.now().minusMillis(1000);
        Reminder reminder = TestReminder.defaultBuilder()
                .id(REMINDER_ID).threadId(THREAD_ID)
                .periodicity(Periodicity.DAILY).lastNotificationDate(instant).build();
        reminderRepository.save(reminder);
        Notification notification = TestNotification.defaultBuilder()
                .reminderId(REMINDER_ID).status(NotificationStatus.SENT).build();
        notificationRepository.save(notification);

        reminderTask.deleteSentNotifications();

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).isEmpty();
    }

}
