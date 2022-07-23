package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.ReminderMailInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "item-service", contextId = "system", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignItemSystemServiceClient"})
public interface ItemSystemServiceClient {

    @GetMapping(value = "/api/member/{groupId}")
    List<UUID> getUserIdsByGroupId(@PathVariable UUID groupId);

    @GetMapping(value = "/api/member/{itemId}/item")
    List<UUID> getUserIdsByItemId(@PathVariable UUID itemId);

    @GetMapping(value = "/api/info/item-reminder/{itemId}")
    ReminderMailInfo getReminderMailInfo(@PathVariable UUID itemId);

}
