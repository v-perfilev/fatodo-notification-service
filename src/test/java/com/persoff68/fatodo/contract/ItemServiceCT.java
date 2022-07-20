package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.TypeAndParent;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
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
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:itemservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class ItemServiceCT {

    @Autowired
    ItemServiceClient itemServiceClient;

    @Test
    @WithCustomSecurityContext
    void testGetGroupIdsForUser() {
        List<UUID> groupIdList = itemServiceClient.getGroupIdsForUser();
        assertThat(groupIdList).isNotEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testHasItemsPermission() {
        List<UUID> targetIdList = Collections.singletonList(UUID.randomUUID());
        boolean canRead = itemServiceClient.hasItemsPermission("READ", targetIdList);
        assertThat(canRead).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testGetTypeAndParent() {
        TypeAndParent typeAndParent = itemServiceClient.getTypeAndParent(UUID.randomUUID());
        assertThat(typeAndParent.getType()).isEqualTo(ReminderThreadType.ITEM);
        assertThat(typeAndParent.getParentId()).isNotNull();
    }

}
