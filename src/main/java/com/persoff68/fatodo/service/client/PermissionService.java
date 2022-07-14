package com.persoff68.fatodo.service.client;

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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ItemServiceClient itemServiceClient;

    public List<UUID> getParentIds() {
        return itemServiceClient.getGroupIdsForUser();
    }

    public void checkThreadsPermission(String permission, Collection<ReminderThread> threadCollection) {
        Multimap<ReminderThreadType, ReminderThread> threadMultimap = threadCollection.stream()
                .collect(Multimaps.toMultimap(
                        ReminderThread::getType,
                        Function.identity(),
                        HashMultimap::create
                ));
        threadMultimap.keySet().forEach(key -> checkSameTypeThreadsPermission(key, permission,
                threadMultimap.get(key)));
    }

    public void checkThreadPermission(String permission, ReminderThread thread) {
        ReminderThreadType type = thread.getType();
        UUID targetId = thread.getTargetId();
        List<UUID> targetIdList = Collections.singletonList(targetId);
        switch (type) {
            case ITEM -> checkItemsPermission(permission, targetIdList);
            default -> throw new PermissionException();
        }
    }

    private void checkSameTypeThreadsPermission(ReminderThreadType type,
                                                String permission,
                                                Collection<ReminderThread> threadCollection) {
        List<UUID> targetIdList = threadCollection.stream().map(ReminderThread::getTargetId).toList();
        switch (type) {
            case ITEM -> checkItemsPermission(permission, targetIdList);
            default -> throw new PermissionException();
        }
    }

    private void checkItemsPermission(String permission, List<UUID> itemIdList) {
        boolean hasPermission = itemServiceClient.hasItemsPermission(permission, itemIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

}
