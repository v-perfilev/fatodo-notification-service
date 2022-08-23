package com.persoff68.fatodo.web.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.ItemSystemServiceClient;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.dto.EventDTO;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.NotificationService;
import com.persoff68.fatodo.service.client.MailService;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "kafka.bootstrapAddress=localhost:9092",
        "kafka.groupId=test",
        "kafka.partitions=1",
        "kafka.autoOffsetResetConfig=earliest"
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class EventProducerIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    NotificationService notificationService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    ReminderRepository reminderRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @MockBean
    ItemSystemServiceClient itemSystemServiceClient;
    @MockBean
    MailService mailService;
    @SpyBean
    EventServiceClient eventServiceClient;

    private ConcurrentMessageListenerContainer<String, EventDTO> eventContainer;
    private BlockingQueue<ConsumerRecord<String, EventDTO>> eventRecords;

    @BeforeEach
    void setup() {
        ReminderThread thread = TestReminderThread.defaultBuilder().build().toParent();
        Date date = Date.from(Instant.now().minusSeconds(10));
        Reminder reminder = TestReminder.defaultBuilder().thread(thread).lastNotificationDate(date).build().toParent();
        Notification notification = TestNotification.defaultBuilder().reminder(reminder).date(date).build().toParent();
        reminder.setNotifications(List.of(notification));
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        doNothing().when(mailService).sendNotification(any());

        startEventConsumer();
    }

    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
        reminderRepository.deleteAll();
        threadRepository.deleteAll();

        stopEventConsumer();
    }

    @Test
    void testAddEvent_ok() throws Exception {
        notificationService.sendNotifications();

        ConsumerRecord<String, EventDTO> record = eventRecords.poll(5, TimeUnit.SECONDS);

        assertThat(eventServiceClient).isInstanceOf(EventProducer.class);
        assertThat(record).isNotNull();
        verify(eventServiceClient).addEvent(any());
    }

    private void startEventConsumer() {
        JavaType javaType = objectMapper.getTypeFactory().constructType(EventDTO.class);
        ConcurrentKafkaListenerContainerFactory<String, EventDTO> containerFactory =
                KafkaUtils.buildJsonContainerFactory(embeddedKafkaBroker.getBrokersAsString(),
                        "test", "earliest", javaType);
        eventContainer = containerFactory.createContainer("event");
        eventRecords = new LinkedBlockingQueue<>();
        eventContainer.setupMessageListener((MessageListener<String, EventDTO>) eventRecords::add);
        eventContainer.start();
        ContainerTestUtils.waitForAssignment(eventContainer, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private void stopEventConsumer() {
        eventContainer.stop();
    }

}
