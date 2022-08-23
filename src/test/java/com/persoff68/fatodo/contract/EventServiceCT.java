package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestEventDTO;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.dto.EventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:eventservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class EventServiceCT {

    @Autowired
    EventServiceClient eventServiceClient;

    @Test
    void testAddReminderEvent() {
        EventDTO dto = TestEventDTO.defaultBuilder().build();
        assertDoesNotThrow(() -> eventServiceClient.addEvent(dto));
    }

}
