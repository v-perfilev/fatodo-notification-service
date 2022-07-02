package com.persoff68.fatodo.config.util;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaUtils {

    public static KafkaAdmin buildKafkaAdmin(String bootstrapAddress) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    public static NewTopic buildTopic(String topicName, int partitions) {
        return new NewTopic(topicName, partitions, (short) 1);
    }

    public static <T> KafkaTemplate<String, T> buildJsonKafkaTemplate(String bootstrapAddress) {
        ProducerFactory<String, T> producerFactory = buildJsonProducerFactory(bootstrapAddress);
        return new KafkaTemplate<>(producerFactory);
    }

    public static <T> ConcurrentKafkaListenerContainerFactory<String, T> buildJsonContainerFactory(
            String bootstrapAddress, String groupId, Class<T> clazz) {
        ConsumerFactory<String, T> consumerFactory = buildJsonConsumerFactory(bootstrapAddress, groupId,
                clazz);
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    private static <T> ProducerFactory<String, T> buildJsonProducerFactory(String bootstrapAddress) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        StringSerializer keySerializer = new StringSerializer();
        JsonSerializer<T> valueSerializer = new JsonSerializer<>();
        valueSerializer.setAddTypeInfo(false);
        return new DefaultKafkaProducerFactory<>(configProps, keySerializer, valueSerializer);
    }

    private static <T> ConsumerFactory<String, T> buildJsonConsumerFactory(
            String bootstrapAddress, String groupId, Class<T> clazz) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        StringDeserializer keyDeserializer = new StringDeserializer();
        JsonDeserializer<T> valueDeserializer = new JsonDeserializer<>(clazz);
        valueDeserializer.trustedPackages("*");
        valueDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
    }

}
