package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.ItemSystemServiceClient;
import com.persoff68.fatodo.model.ReminderInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:itemservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class ItemSystemServiceCT {

    @Autowired
    ItemSystemServiceClient itemSystemServiceClient;

    @Test
    void testGetReminderMailInfo() {
        ReminderInfo message = itemSystemServiceClient.getReminderMailInfo(UUID.randomUUID());
        assertThat(message).isNotNull();
    }

}
