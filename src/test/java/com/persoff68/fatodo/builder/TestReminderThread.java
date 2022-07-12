package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.Builder;

import java.util.UUID;

public class TestReminderThread extends ReminderThread {

    @Builder
    TestReminderThread(UUID parentId, UUID targetId, ReminderThreadType type) {
        super();
        setParentId(parentId);
        setTargetId(targetId);
        setType(type);
    }

    public static TestReminderThreadBuilder defaultBuilder() {
        return TestReminderThread.builder()
                .parentId(UUID.randomUUID())
                .targetId(UUID.randomUUID())
                .type(ReminderThreadType.ITEM);
    }

    public ReminderThread toParent() {
        ReminderThread thread = new ReminderThread();
        thread.setParentId(getParentId());
        thread.setTargetId(getTargetId());
        thread.setType(getType());
        return thread;
    }

}
