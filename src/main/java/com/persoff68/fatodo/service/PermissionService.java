package com.persoff68.fatodo.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ItemServiceClient itemServiceClient;

    public void checkThreadReadPermission(ReminderThread thread) {
        ReminderThreadType type = thread.getType();
        if (type == ReminderThreadType.ITEM) {
            checkItemReadPermission(thread);
        } else {
            throw new PermissionException();
        }
    }

    public void checkThreadEditPermission(ReminderThread thread) {
        ReminderThreadType type = thread.getType();
        if (type == ReminderThreadType.ITEM) {
            checkItemEditPermission(thread);
        } else {
            throw new PermissionException();
        }
    }

    public void checkThreadsEditPermission(Collection<ReminderThread> threadCollection) {
        Multimap<ReminderThreadType, ReminderThread> threadMultimap = threadCollection.stream()
                .collect(Multimaps.toMultimap(
                        ReminderThread::getType,
                        Function.identity(),
                        HashMultimap::create
                ));
        threadMultimap.keySet().forEach(key -> checkSameTypeThreadsEditPermission(key, threadMultimap.get(key)));
    }

    private void checkSameTypeThreadsEditPermission(ReminderThreadType type,
                                                    Collection<ReminderThread> threadCollection) {
        switch (type) {
            case ITEM -> checkItemsEditPermission(threadCollection);
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
        boolean hasPermission = itemServiceClient.canEditItem(targetId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    private void checkItemsEditPermission(Collection<ReminderThread> threadCollection) {
        List<UUID> itemIdList = threadCollection.stream()
                .map(ReminderThread::getTargetId)
                .toList();
        boolean hasPermission = itemServiceClient.canEditItems(itemIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

}
