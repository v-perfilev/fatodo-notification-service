package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.ReminderMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:itemservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class ItemServiceCT {

    @Autowired
    ItemServiceClient itemServiceClient;

    @Test
    void testCanReadItem() {
        boolean canRead = itemServiceClient.canReadItem(UUID.randomUUID());
        assertThat(canRead).isTrue();
    }

    @Test
    void testCanEditItem() {
        boolean canEdit = itemServiceClient.canEditItem(UUID.randomUUID());
        assertThat(canEdit).isTrue();
    }

    @Test
    void testIsItem() {
        boolean isItem = itemServiceClient.isItem(UUID.randomUUID());
        assertThat(isItem).isTrue();
    }

    @Test
    void testGetUserIdsByGroupId() {
        List<UUID> userIdList = itemServiceClient.getUserIdsByGroupId(UUID.randomUUID());
        assertThat(userIdList).isNotEmpty();
    }

    @Test
    void testGetUserIdsByItemId() {
        List<UUID> userIdList = itemServiceClient.getUserIdsByItemId(UUID.randomUUID());
        assertThat(userIdList).isNotEmpty();
    }

    @Test
    void testGetReminderByItemId() {
        ReminderMessage message = itemServiceClient.getReminderByItemId(UUID.randomUUID());
        assertThat(message).isNotNull();
    }

}
