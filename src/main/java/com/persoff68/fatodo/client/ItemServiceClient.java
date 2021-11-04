package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.ReminderMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "item-service", primary = false)
public interface ItemServiceClient {

    @GetMapping(value = "/api/permissions/item/read/{itemId}")
    boolean canReadItem(@PathVariable UUID itemId);

    @GetMapping(value = "/api/permissions/item/edit/{itemId}")
    boolean canEditItem(@PathVariable UUID itemId);

    @GetMapping(value = "/api/check/is-item/{itemId}")
    boolean isItem(@PathVariable UUID itemId);

    @GetMapping(value = "/api/members/group/{groupId}/ids")
    List<UUID> getUserIdsByGroupId(@PathVariable UUID groupId);

    @GetMapping(value = "/api/members/item/{itemId}/ids")
    List<UUID> getUserIdsByItemId(@PathVariable UUID itemId);

    @GetMapping(value = "/api/reminders/item/{itemId}")
    ReminderMessage getReminderByItemId(@PathVariable UUID itemId);
}