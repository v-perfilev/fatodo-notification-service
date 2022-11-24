package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemSystemServiceClientWrapper implements ItemSystemServiceClient {

    @Qualifier("feignItemSystemServiceClient")
    private final ItemSystemServiceClient itemSystemServiceClient;

    @Override
    public ReminderInfo getReminderMailInfo(UUID itemId) {
        try {
            return itemSystemServiceClient.getReminderMailInfo(itemId);
        } catch (FeignException.NotFound e) {
            throw new ModelNotFoundException();
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
