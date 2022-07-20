package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemSystemServiceClientWrapper implements ItemSystemServiceClient {

    @Qualifier("feignItemSystemServiceClient")
    private final ItemSystemServiceClient itemSystemServiceClient;

    @Override
    public List<UUID> getUserIdsByGroupId(UUID groupId) {
        try {
            return itemSystemServiceClient.getUserIdsByGroupId(groupId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public List<UUID> getUserIdsByItemId(UUID itemId) {
        try {
            return itemSystemServiceClient.getUserIdsByItemId(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public ReminderMailInfo getReminderMailInfo(UUID itemId) {
        try {
            return itemSystemServiceClient.getReminderMailInfo(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
