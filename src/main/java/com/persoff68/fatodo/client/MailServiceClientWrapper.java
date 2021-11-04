package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Primary
@RequiredArgsConstructor
public class MailServiceClientWrapper implements MailServiceClient {

    @Qualifier("mailServiceClient")
    private final MailServiceClient mailServiceClient;

    @Override
    public void sendNotification(NotificationMail notificationMail) {
        try {
            mailServiceClient.sendNotification(notificationMail);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
