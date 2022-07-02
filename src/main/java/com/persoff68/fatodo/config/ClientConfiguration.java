package com.persoff68.fatodo.config;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {

    private final BeanFactory beanFactory;

    @Bean
    @Primary
    public ItemServiceClient itemClient() {
        return (ItemServiceClient) beanFactory.getBean("itemServiceClientWrapper");
    }

    @Bean
    @Primary
    public MailServiceClient mailClient() {
        boolean kafkaProducerExists = beanFactory.containsBean("mailProducer");
        return kafkaProducerExists
                ? (MailServiceClient) beanFactory.getBean("mailProducer")
                : (MailServiceClient) beanFactory.getBean("mailServiceClientWrapper");
    }

    @Bean
    @Primary
    public UserServiceClient userClient() {
        return (UserServiceClient) beanFactory.getBean("userServiceClientWrapper");
    }

}
