package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.dto.CreateReminderEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "event-service", primary = false, qualifiers = {"feignEventServiceClient"})
public interface EventServiceClient {

    @PostMapping(value = "/api/events/reminder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void addReminderEvent(@RequestBody CreateReminderEventDTO createReminderEventDTO);

}
