package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.NotificationMail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mail-service", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignMailServiceClient"})
public interface MailServiceClient {

    @PostMapping(value = "/api/mails/notification")
    void sendNotification(@RequestBody NotificationMail notificationMail);

}
