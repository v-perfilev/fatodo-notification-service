package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderThreadService {

    private final ReminderThreadRepository threadRepository;
    private final PermissionService permissionService;
    private final ItemServiceClient itemServiceClient;

    public ReminderThread getByTargetIdOrCreate(UUID targetId) {
        try {
            ReminderThread thread = threadRepository.findByTargetId(targetId)
                    .orElseThrow(ModelNotFoundException::new);
            permissionService.checkThreadEditPermission(thread);
            return thread;
        } catch (ModelNotFoundException e) {
            ReminderThreadType type = getTypeByTargetId(targetId);
            ReminderThread thread = ReminderThread.of(targetId, type);
            permissionService.checkThreadEditPermission(thread);
            return threadRepository.save(thread);
        }
    }

    public ReminderThread getByTargetId(UUID targetId) {
        ReminderThread thread = threadRepository.findByTargetId(targetId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkThreadReadPermission(thread);
        return thread;
    }

    public ReminderThread deleteByTargetId(UUID targetId) {
        ReminderThread thread = threadRepository.findByTargetId(targetId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkThreadEditPermission(thread);
        threadRepository.deleteByTargetId(targetId);
        return thread;
    }

    private ReminderThreadType getTypeByTargetId(UUID targetId) {
        boolean isItem = itemServiceClient.isItem(targetId);
        if (isItem) {
            return ReminderThreadType.ITEM;
        }
        throw new ModelNotFoundException();
    }
}
