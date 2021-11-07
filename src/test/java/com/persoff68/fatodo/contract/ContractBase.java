package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ContractBase {

    private static final String TARGET_ID = "fc2c6859-dcdb-470d-9fc6-cf21a1bf98b0";
    private static final String THREAD_ID = "df09ce60-c3cd-4355-b136-3ccc0698dbf5";

    @Autowired
    WebApplicationContext context;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    ReminderRepository reminderRepository;
    @MockBean
    ItemServiceClient itemServiceClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        reminderRepository.deleteAll();
        threadRepository.deleteAll();

        UUID threadId = UUID.fromString(THREAD_ID);
        UUID targetId = UUID.fromString(TARGET_ID);
        ReminderThread thread = TestReminderThread.defaultBuilder().id(threadId).targetId(targetId).build();
        threadRepository.save(thread);
        Reminder reminder = TestReminder.defaultBuilder().threadId(threadId).build();
        reminderRepository.save(reminder);
    }

}
