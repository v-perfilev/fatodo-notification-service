package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.Builder;

import java.util.UUID;

public class TestReminderThread extends ReminderThread {

    @Builder
    TestReminderThread(UUID id, UUID targetId, ReminderThreadType type) {
        super();
        setId(id);
        setTargetId(targetId);
        setType(type);
    }

    public static TestReminderThreadBuilder defaultBuilder() {
        return TestReminderThread.builder()
                .id(UUID.randomUUID())
                .targetId(UUID.randomUUID())
                .type(ReminderThreadType.ITEM);
    }

}
