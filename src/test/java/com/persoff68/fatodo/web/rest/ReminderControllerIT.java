package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Multimap;
import com.persoff68.fatodo.FatodoNotificationServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestDateParams;
import com.persoff68.fatodo.builder.TestNotification;
import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.builder.TestReminderDTO;
import com.persoff68.fatodo.builder.TestReminderThread;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.TypeAndParent;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.model.dto.CalendarReminderDTO;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.repository.NotificationRepository;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoNotificationServiceApplication.class)
@AutoConfigureMockMvc
class ReminderControllerIT {
    private static final String ENDPOINT = "/api/reminder";

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

    ReminderThread thread;


    @BeforeEach
    void setup() {
        thread = TestReminderThread.defaultBuilder().parentId(PARENT_ID).targetId(TARGET_ID).build().toParent();
        Reminder reminder = TestReminder.defaultBuilder().thread(thread)
                .periodicity(Periodicity.DAILY)
                .build().toParent();
        Notification notification = TestNotification.defaultBuilder().reminder(reminder).build().toParent();
        reminder.setNotifications(List.of(notification));
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        TypeAndParent typeAndParent = new TypeAndParent(ReminderThreadType.ITEM, UUID.randomUUID());
        when(itemServiceClient.getTypeAndParent(any())).thenReturn(typeAndParent);
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(true);
        when(itemServiceClient.getGroupIdsForUser()).thenReturn(List.of(PARENT_ID));
    }

    @AfterEach
    void cleanup() {
        reminderRepository.deleteAll();
        threadRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByMonths_ok() throws Exception {
        String url = ENDPOINT + "/calendar?yearFrom=2090&monthFrom=0&yearTo=2090&monthTo=1&timezone=Europe/Berlin";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructMapLikeType(Multimap.class, String.class,
                CalendarReminderDTO.class);
        Multimap<String, CalendarReminderDTO> resultMultimap = objectMapper.readValue(resultString, javaType);
        assertThat(resultMultimap.keySet()).hasSize(2);
        assertThat(resultMultimap.get("2090_0")).hasSize(31);
        assertThat(resultMultimap.get("2090_1")).hasSize(28);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByMonths_ok_weekly() throws Exception {
        Reminder reminder = TestReminder.defaultBuilder().thread(thread)
                .periodicity(Periodicity.WEEKLY)
                .weekDays(List.of(0))
                .build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        String url = ENDPOINT + "/calendar?yearFrom=2090&monthFrom=0&yearTo=2090&monthTo=1&timezone=Europe/Berlin";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructMapLikeType(Multimap.class, String.class,
                CalendarReminderDTO.class);
        Multimap<String, CalendarReminderDTO> resultMultimap = objectMapper.readValue(resultString, javaType);
        assertThat(resultMultimap.keySet()).hasSize(2);
        assertThat(resultMultimap.get("2090_0")).hasSize(5);
        assertThat(resultMultimap.get("2090_1")).hasSize(4);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByMonths_ok_monthly() throws Exception {
        Reminder reminder = TestReminder.defaultBuilder().thread(thread)
                .periodicity(Periodicity.MONTHLY)
                .monthDays(List.of(1))
                .build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        String url = ENDPOINT + "/calendar?yearFrom=2090&monthFrom=0&yearTo=2090&monthTo=1&timezone=Europe/Berlin";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructMapLikeType(Multimap.class, String.class,
                CalendarReminderDTO.class);
        Multimap<String, CalendarReminderDTO> resultMultimap = objectMapper.readValue(resultString, javaType);
        assertThat(resultMultimap.keySet()).hasSize(2);
        assertThat(resultMultimap.get("2090_0")).hasSize(1);
        assertThat(resultMultimap.get("2090_1")).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByMonths_ok_yearly() throws Exception {
        DateParams dateParams = TestDateParams.defaultBuilder().date(10).month(1).build().toParent();
        Reminder reminder = TestReminder.defaultBuilder().thread(thread)
                .periodicity(Periodicity.YEARLY)
                .date(dateParams)
                .build().toParent();
        thread.setReminders(List.of(reminder));
        threadRepository.save(thread);

        String url = ENDPOINT + "/calendar?yearFrom=2090&monthFrom=0&yearTo=2090&monthTo=1&timezone=Europe/Berlin";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructMapLikeType(Multimap.class, String.class,
                CalendarReminderDTO.class);
        Multimap<String, CalendarReminderDTO> resultMultimap = objectMapper.readValue(resultString, javaType);
        assertThat(resultMultimap.keySet()).hasSize(1);
        assertThat(resultMultimap.get("2090_1")).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByMonths_invalid() throws Exception {
        String url = ENDPOINT + "/calendar?yearFrom=2090&monthFrom=0&monthTo=1&timezone=Europe/Berlin";
        mvc.perform(get(url))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByMonth_unauthorized() throws Exception {
        String url = ENDPOINT + "/calendar?year=2090&month=0&timezone=Europe/Berlin";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByTargetId_ok() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                ReminderDTO.class);
        List<ReminderDTO> resultList = objectMapper.readValue(resultString, collectionType);
        assertThat(resultList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByTargetId_notFound() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByTargetId_forbidden() throws Exception {
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(false);
        String url = ENDPOINT + "/" + TARGET_ID;
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByTargetId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext
    void testSetReminders_ok_update() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID;
        ReminderDTO dto = TestReminderDTO.defaultBuilder().build().toParent();
        List<ReminderDTO> dtoList = Collections.singletonList(dto);
        String requestBody = objectMapper.writeValueAsString(dtoList);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        List<Reminder> reminderList = reminderRepository.findAll();
        assertThat(reminderList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testSetReminders_ok_create() throws Exception {
        String url = ENDPOINT + "/" + UUID.randomUUID();
        ReminderDTO dto = TestReminderDTO.defaultBuilder().build().toParent();
        List<ReminderDTO> dtoList = Collections.singletonList(dto);
        String requestBody = objectMapper.writeValueAsString(dtoList);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @WithCustomSecurityContext
    void testSetReminders_notFound() throws Exception {
        doThrow(new ModelNotFoundException()).when(itemServiceClient).getTypeAndParent(any());
        String url = ENDPOINT + "/" + UUID.randomUUID();
        ReminderDTO dto = TestReminderDTO.defaultBuilder().build().toParent();
        List<ReminderDTO> dtoList = Collections.singletonList(dto);
        String requestBody = objectMapper.writeValueAsString(dtoList);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
        List<Reminder> reminderList = reminderRepository.findAll();
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(reminderList).hasSize(1);
        assertThat(notificationList).isNotEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testSetReminders_forbidden() throws Exception {
        when(itemServiceClient.hasItemsPermission(any(), any())).thenReturn(false);
        String url = ENDPOINT + "/" + TARGET_ID;
        ReminderDTO dto = TestReminderDTO.defaultBuilder().build().toParent();
        List<ReminderDTO> dtoList = Collections.singletonList(dto);
        String requestBody = objectMapper.writeValueAsString(dtoList);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testSetReminders_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + TARGET_ID;
        ReminderDTO dto = TestReminderDTO.defaultBuilder().build().toParent();
        List<ReminderDTO> dtoList = Collections.singletonList(dto);
        String requestBody = objectMapper.writeValueAsString(dtoList);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
