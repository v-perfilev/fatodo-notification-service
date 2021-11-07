package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ReminderMessage;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestReminderMessage extends ReminderMessage {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestReminderMessage(String message,
                        String url,
                        List<UUID> userIds) {
        setMessage(message);
        setUrl(url);
        setUserIds(userIds);
    }

    public static TestReminderMessageBuilder defaultBuilder() {
        return TestReminderMessage.builder()
                .message(DEFAULT_VALUE)
                .url(DEFAULT_VALUE)
                .userIds(Collections.singletonList(UUID.randomUUID()));
    }

}
