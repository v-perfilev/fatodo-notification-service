package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.NotificationMail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailServiceClientWrapper implements MailServiceClient {

    @Qualifier("feignMailServiceClient")
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
