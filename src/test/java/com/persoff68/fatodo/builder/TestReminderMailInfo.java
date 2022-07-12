package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ReminderMailInfo;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestReminderMailInfo extends ReminderMailInfo {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestReminderMailInfo(String message,
                         String url,
                         List<UUID> userIds) {
        setMessage(message);
        setUrl(url);
        setUserIds(userIds);
    }

    public static TestReminderMailInfoBuilder defaultBuilder() {
        return TestReminderMailInfo.builder()
                .message(DEFAULT_VALUE)
                .url(DEFAULT_VALUE)
                .userIds(Collections.singletonList(UUID.randomUUID()));
    }

    public ReminderMailInfo toParent() {
        ReminderMailInfo message = new ReminderMailInfo();
        message.setMessage(getMessage());
        message.setUrl(getUrl());
        message.setUserIds(getUserIds());
        return message;
    }

}
