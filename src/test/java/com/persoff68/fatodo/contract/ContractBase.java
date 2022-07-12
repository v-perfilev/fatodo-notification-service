package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {

    private static final String PARENT_ID = "df09ce60-c3cd-4355-b136-3ccc0698dbf5";
    private static final String TARGET_ID = "fc2c6859-dcdb-470d-9fc6-cf21a1bf98b0";

    @Autowired
    WebApplicationContext context;

    @Autowired
    ReminderThreadRepository threadRepository;
    @MockBean
    ItemServiceClient itemServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        threadRepository.deleteAll();

        UUID threadId = UUID.fromString(PARENT_ID);
        UUID targetId = UUID.fromString(TARGET_ID);
        ReminderThread thread = TestReminderThread.defaultBuilder().parentId(threadId).targetId(targetId).build().toParent();
        Reminder reminder = TestReminder.defaultBuilder().thread(thread).build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(true);
    }

}
