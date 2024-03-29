package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.UserSystemServiceClient;
import com.persoff68.fatodo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:userservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class UserServiceCT {

    @Autowired
    UserSystemServiceClient userSystemServiceClient;

    @Test
    void testGetAllInfoByIds() {
        List<UUID> userIdList = Collections.singletonList(UUID.randomUUID());
        List<User> userList = userSystemServiceClient.getAllUserDataByIds(userIdList);
        assertThat(userList).isNotEmpty();
    }

}
