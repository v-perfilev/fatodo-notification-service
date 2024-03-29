package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.constant.KafkaTopics;
import com.persoff68.fatodo.model.NotificationMail;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class MailProducer implements MailServiceClient {

    private final KafkaTemplate<String, NotificationMail> notificationMailKafkaTemplate;

    public void sendNotification(NotificationMail notificationMail) {
        notificationMailKafkaTemplate.send(KafkaTopics.MAIL_NOTIFICATION.getValue(), notificationMail);
    }

}
