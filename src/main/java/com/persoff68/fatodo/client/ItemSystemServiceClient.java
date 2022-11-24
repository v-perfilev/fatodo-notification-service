package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.ReminderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "item-service", contextId = "system", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignItemSystemServiceClient"})
public interface ItemSystemServiceClient {

    @GetMapping(value = "/api/info/item-reminder/{itemId}")
    ReminderInfo getReminderMailInfo(@PathVariable UUID itemId);

}
