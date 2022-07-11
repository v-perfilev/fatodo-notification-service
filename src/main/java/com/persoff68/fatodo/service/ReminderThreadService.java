package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.TypeAndParent;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
            TypeAndParent typeAndParent = getTypeByTargetId(targetId);
            UUID parentId = typeAndParent.getParentId();
            ReminderThreadType type = typeAndParent.getType();
            ReminderThread thread = ReminderThread.of(parentId, targetId, type);
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

    public void deleteByParentId(UUID parentId) {
        List<ReminderThread> threadList = threadRepository.findAllByParentId(parentId);
        if (!threadList.isEmpty()) {
            permissionService.checkThreadsEditPermission(threadList);
            threadRepository.deleteAll(threadList);
        }
    }

    public void deleteByTargetId(UUID targetId) {
        Optional<ReminderThread> threadOptional = threadRepository.findByTargetId(targetId);
        if (threadOptional.isPresent()) {
            ReminderThread thread = threadOptional.get();
            permissionService.checkThreadEditPermission(thread);
            threadRepository.delete(thread);
        }
    }

    private TypeAndParent getTypeByTargetId(UUID targetId) {
        return itemServiceClient.getTypeAndParent(targetId);
    }
}
