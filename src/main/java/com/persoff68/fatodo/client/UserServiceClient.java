package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", primary = false, qualifiers = {"feignUserServiceClient"})
public interface UserServiceClient {

    @PostMapping(value = "/api/info/info")
    List<UserInfo> getAllInfoByIds(@RequestBody List<UUID> userIdList);

}

