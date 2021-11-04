package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ItemServiceClient itemServiceClient;

    public void checkThreadReadPermission(ReminderThread thread) {
        ReminderThreadType type = thread.getType();
        switch (type) {
            case ITEM -> checkItemReadPermission(thread);
            default -> throw new PermissionException();
        }
    }

    public void checkThreadEditPermission(ReminderThread thread) {
        ReminderThreadType type = thread.getType();
        switch (type) {
            case ITEM -> checkItemEditPermission(thread);
            default -> throw new PermissionException();
        }
    }

    private void checkItemReadPermission(ReminderThread thread) {
        UUID targetId = thread.getTargetId();
        boolean hasPermission = itemServiceClient.canReadItem(targetId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    private void checkItemEditPermission(ReminderThread thread) {
        UUID targetId = thread.getTargetId();
        boolean hasPermission = itemServiceClient.canReadItem(targetId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

}