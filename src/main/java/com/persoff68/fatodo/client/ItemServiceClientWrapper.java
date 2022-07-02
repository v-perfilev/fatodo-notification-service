package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.ReminderMessage;
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
    public boolean canReadItem(UUID itemId) {
        try {
            return itemServiceClient.canReadItem(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public boolean canEditItem(UUID itemId) {
        try {
            return itemServiceClient.canEditItem(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public boolean isItem(UUID itemId) {
        try {
            return itemServiceClient.isItem(itemId);
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
    public ReminderMessage getReminderByItemId(UUID itemId) {
        try {
            return itemServiceClient.getReminderByItemId(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
