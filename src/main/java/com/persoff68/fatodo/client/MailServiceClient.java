package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.NotificationMail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MailServiceClient {

    @PostMapping(value = "/notification")
    void sendNotification(@RequestBody NotificationMail notificationMail);

}
