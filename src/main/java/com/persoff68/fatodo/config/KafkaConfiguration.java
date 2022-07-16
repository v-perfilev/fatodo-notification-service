package com.persoff68.fatodo.config;

import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.constant.KafkaTopics;
import com.persoff68.fatodo.config.util.KafkaUtils;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.dto.CreateReminderEventDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class KafkaConfiguration {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.partitions}")
    private int partitions;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return KafkaUtils.buildKafkaAdmin(bootstrapAddress);
    }

    @Bean
    public NewTopic notificationNewTopic() {
        return KafkaUtils.buildTopic(KafkaTopics.MAIL_NOTIFICATION.getValue(), partitions);
    }

    @Bean
    public NewTopic eventNewTopic() {
        return KafkaUtils.buildTopic(KafkaTopics.EVENT_ADD.getValue(), partitions);
    }

    @Bean
    public KafkaTemplate<String, NotificationMail> notificationMailKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

    @Bean
    public KafkaTemplate<String, CreateReminderEventDTO> eventReminderKafkaTemplate() {
        return KafkaUtils.buildJsonKafkaTemplate(bootstrapAddress);
    }

}
