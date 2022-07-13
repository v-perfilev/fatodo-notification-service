package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.model.TypeAndParent;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemServiceClientWrapper implements ItemServiceClient {

    @Qualifier("feignItemServiceClient")
    private final ItemServiceClient itemServiceClient;

    @Override
    public List<UUID> getGroupIdsForUser() {
        try {
            return itemServiceClient.getGroupIdsForUser();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public boolean hasItemsPermission(String permission, List<UUID> itemIdList) {
        try {
            return itemServiceClient.hasItemsPermission(permission, itemIdList);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public TypeAndParent getTypeAndParent(UUID id) {
        try {
            return itemServiceClient.getTypeAndParent(id);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public List<UUID> getUserIdsByGroupId(UUID groupId) {
        try {
            return itemServiceClient.getUserIdsByGroupId(groupId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public List<UUID> getUserIdsByItemId(UUID itemId) {
        try {
            return itemServiceClient.getUserIdsByItemId(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public ReminderMailInfo getReminderMailInfo(UUID itemId) {
        try {
            return itemServiceClient.getReminderMailInfo(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
