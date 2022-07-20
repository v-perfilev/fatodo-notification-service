package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderMailInfo;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.builder.TestUserInfo;
import com.persoff68.fatodo.client.ItemSystemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderMailInfo;
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
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class MailProducerIT {

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
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemSystemServiceClient itemSystemServiceClient;
    @MockBean
    UserServiceClient userServiceClient;

    @SpyBean
    MailServiceClient mailServiceClient;

    private ConcurrentMessageListenerContainer<String, NotificationMail> notificationContainer;
    private BlockingQueue<ConsumerRecord<String, NotificationMail>> notificationRecords;

    ReminderThread thread;

    @BeforeEach
    void setup() {
        thread = TestReminderThread.defaultBuilder().build().toParent();
        threadRepository.save(thread);

        ReminderMailInfo message = TestReminderMailInfo.defaultBuilder().build().toParent();
        when(itemSystemServiceClient.getReminderMailInfo(any())).thenReturn(message);
        UserInfo userInfo = TestUserInfo.defaultBuilder().build().toParent();
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
        Date date = Date.from(Instant.now().minusSeconds(10));
        Reminder reminder = TestReminder.defaultBuilder().thread(thread).lastNotificationDate(date).build().toParent();
        Notification notification = TestNotification.defaultBuilder().reminder(reminder).date(date).build().toParent();
        reminder.setNotifications(List.of(notification));
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        reminderTask.sendNotifications();
        ConsumerRecord<String, NotificationMail> record = notificationRecords.poll(10, TimeUnit.SECONDS);

        assertThat(mailServiceClient).isInstanceOf(MailProducer.class);
        assertThat(record).isNotNull();
        verify(mailServiceClient).sendNotification(any());
    }

    private void startNotificationConsumer() {
        JavaType javaType = objectMapper.getTypeFactory().constructType(NotificationMail.class);
        ConcurrentKafkaListenerContainerFactory<String, NotificationMail> notificationContainerFactory =
                KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(), "test", javaType);
        notificationContainer = notificationContainerFactory.createContainer("mail_notification");
        notificationRecords = new LinkedBlockingQueue<>();
        notificationContainer.setupMessageListener((MessageListener<String, NotificationMail>) notificationRecords::add);
        notificationContainer.start();
        ContainerTestUtils.waitForAssignment(notificationContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopNotificationConsumer() {
        notificationContainer.stop();
    }

}
