package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoNotificationServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.TypeAndParent;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoNotificationServiceApplication.class)
@AutoConfigureMockMvc
class ReminderThreadControllerIT {
    private static final String ENDPOINT = "/api/threads";

    private static final UUID PARENT_ID = UUID.randomUUID();
    private static final UUID TARGET_ID = UUID.randomUUID();

    @Autowired
    MockMvc mvc;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    ReminderRepository reminderRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemServiceClient itemServiceClient;

    @BeforeEach
    void setup() {
        ReminderThread thread =
                TestReminderThread.defaultBuilder().parentId(PARENT_ID).targetId(TARGET_ID).build().toParent();
        Reminder reminder = TestReminder.defaultBuilder().thread(thread).build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        TypeAndParent typeAndParent = new TypeAndParent(ReminderThreadType.ITEM, UUID.randomUUID());
        when(itemServiceClient.getTypeAndParent(any())).thenReturn(typeAndParent);
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(true);
    }

    @AfterEach
    void cleanup() {
        threadRepository.deleteAll();
    }


    @Test
    @WithCustomSecurityContext
    void testDeleteRemindersByParentIdByParentId_ok() throws Exception {
        String url = ENDPOINT + "/" + PARENT_ID + "/parent";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).isEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testDeleteRemindersByParentId_forbidden() throws Exception {
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(false);
        String url = ENDPOINT + "/" + PARENT_ID + "/parent";
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDeleteRemindersByParentId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + PARENT_ID + "/parent";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testDeleteRemindersByTargetIdByTargetId_ok() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID + "/target";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).isEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testDeleteRemindersByTargetId_forbidden() throws Exception {
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(false);
        String url = ENDPOINT + "/" + TARGET_ID + "/target";
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDeleteRemindersByTargetId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID + "/target";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
