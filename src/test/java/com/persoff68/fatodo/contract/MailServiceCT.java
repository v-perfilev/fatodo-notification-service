package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestNotificationMail;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.model.NotificationMail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:mailservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class MailServiceCT {

    @Autowired
    MailServiceClient mailServiceClient;

    @Test
    void testSendNotification() {
        NotificationMail dto = TestNotificationMail.defaultBuilder().build();
        mailServiceClient.sendNotification(dto);
        assertThat(true).isTrue();
    }

}
