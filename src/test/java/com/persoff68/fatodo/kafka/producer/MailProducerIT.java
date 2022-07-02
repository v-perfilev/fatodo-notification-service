package com.persoff68.fatodo.kafka.producer;

import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderMessage;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.builder.TestUserInfo;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderMessage;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.UserInfo;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.task.ReminderTask;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=PLAINTEXT://localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class MailProducerIT {

    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final UUID THREAD_ID = UUID.randomUUID();
    private static final UUID REMINDER_ID = UUID.randomUUID();

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

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

    @SpyBean
    MailServiceClient mailServiceClient;

    private ConcurrentMessageListenerContainer<String, NotificationMail> notificationContainer;
    private BlockingQueue<ConsumerRecord<String, NotificationMail>> notificationRecords;


    @BeforeEach
    void setup() {
        ReminderThread thread = TestReminderThread.defaultBuilder().id(THREAD_ID).targetId(TARGET_ID).build();
        threadRepository.save(thread);

        ReminderMessage message = TestReminderMessage.defaultBuilder().build();
        when(itemServiceClient.getReminderByItemId(any())).thenReturn(message);
        UserInfo userInfo = TestUserInfo.defaultBuilder().build();
        when(userServiceClient.getAllInfoByIds(any())).thenReturn(Collections.singletonList(userInfo));

        startNotificationConsumer();
    }

    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
        reminderRepository.deleteAll();
        threadRepository.deleteAll();

        stopNotificationConsumer();
    }

    @Test
    void testSendNotifications() throws InterruptedException {
        Instant instant = Instant.now().minusSeconds(10);
        Reminder reminder =
                TestReminder.defaultBuilder().id(REMINDER_ID).threadId(THREAD_ID).lastNotificationDate(instant).build();
        reminderRepository.save(reminder);
        Notification notification = TestNotification.defaultBuilder().reminderId(REMINDER_ID).date(instant).build();
        notificationRepository.save(notification);

        reminderTask.sendNotifications();
        ConsumerRecord<String, NotificationMail> record = notificationRecords.poll(10, TimeUnit.SECONDS);

        assertThat(mailServiceClient instanceof MailProducer).isTrue();
        assertThat(record).isNotNull();
        verify(mailServiceClient).sendNotification(any());
    }

    private void startNotificationConsumer() {
        notificationContainer = KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test",
                NotificationMail.class).createContainer("mail_notification");
        notificationRecords = new LinkedBlockingQueue<>();
        notificationContainer.setupMessageListener((MessageListener<String, NotificationMail>) notificationRecords::add);
        notificationContainer.start();
        ContainerTestUtils.waitForAssignment(notificationContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopNotificationConsumer() {
        notificationContainer.stop();
    }

}
