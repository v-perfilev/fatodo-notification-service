package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ReminderInfo;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestReminderInfo extends ReminderInfo {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestReminderInfo(UUID groupId,
                     UUID itemId,
                     String message,
                     String url,
                     List<UUID> userIds) {
        setGroupId(groupId);
        setItemId(itemId);
        setMessage(message);
        setUrl(url);
        setUserIds(userIds);
    }

    public static TestReminderInfoBuilder defaultBuilder() {
        return TestReminderInfo.builder()
                .groupId(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .message(DEFAULT_VALUE)
                .url(DEFAULT_VALUE)
                .userIds(Collections.singletonList(UUID.randomUUID()));
    }

    public ReminderInfo toParent() {
        ReminderInfo info = new ReminderInfo();
        info.setGroupId(getGroupId());
        info.setItemId(getItemId());
        info.setMessage(getMessage());
        info.setUrl(getUrl());
        info.setUserIds(getUserIds());
        return info;
    }

}
