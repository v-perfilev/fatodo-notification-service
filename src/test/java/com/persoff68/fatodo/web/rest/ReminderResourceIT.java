package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoNotificationServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.dto.ReminderDTO;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoNotificationServiceApplication.class)
@AutoConfigureMockMvc
public class ReminderResourceIT {
    private static final String ENDPOINT = "/api/reminders";

    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final UUID THREAD_ID = UUID.randomUUID();

    @Autowired
    MockMvc mvc;

    @Autowired
    ReminderThreadRepository threadRepository;
    @Autowired
    ReminderRepository reminderRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemServiceClient itemServiceClient;

    @BeforeEach
    public void setup() {
        ReminderThread thread = TestReminderThread.defaultBuilder().id(THREAD_ID).targetId(TARGET_ID).build();
        threadRepository.save(thread);
        Reminder reminder = TestReminder.defaultBuilder().threadId(THREAD_ID).build();
        reminderRepository.save(reminder);
    }

    @AfterEach
    public void cleanup() {
        reminderRepository.deleteAll();
        threadRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext
    public void testGetAllByTargetId_ok() throws Exception {
        when(itemServiceClient.canReadItem(any())).thenReturn(true);
        String url = ENDPOINT + "/" + TARGET_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ReminderDTO.class);
        List<ReminderDTO> resultList = objectMapper.readValue(resultString, collectionType);
        assertThat(resultList.size()).isEqualTo(1);
    }

    @Test
    @WithCustomSecurityContext
    public void testGetAllByTargetId_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    public void testGetAllByTargetId_forbidden() throws Exception {
        when(itemServiceClient.canReadItem(any())).thenReturn(false);
        String url = ENDPOINT + "/" + TARGET_ID;
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void testGetAllByTargetId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
