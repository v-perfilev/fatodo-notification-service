package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignAuthConfiguration;
import com.persoff68.fatodo.model.TypeAndParent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "item-service", contextId = "auth", primary = false,
        configuration = {FeignAuthConfiguration.class},
        qualifiers = {"feignItemServiceClient"})
public interface ItemServiceClient {

    @GetMapping(value = "/api/permissions/groups")
    List<UUID> getGroupIdsForUser();

    @PostMapping(value = "/api/permissions/items/{permission}")
    boolean hasItemsPermission(@PathVariable String permission, @RequestBody List<UUID> itemIdList);

    @GetMapping(value = "/api/check/type-and-parent/{id}")
    TypeAndParent getTypeAndParent(@PathVariable UUID id);

}
