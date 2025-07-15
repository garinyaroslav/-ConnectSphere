package org.garin.subscriptionservice.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.garin.subscriptionservice.model.CreatePostKafkaEvent;
import org.garin.subscriptionservice.model.SubscriptionChangeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaConsumerConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${app.kafka.groupId}")
  private String groupId;

  @Bean
  public ConsumerFactory<String, CreatePostKafkaEvent> kafkaCreatePostConsumerFactory(
      ObjectMapper objectMapper) {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CreatePostKafkaEvent.class.getName());
    config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
        new JsonDeserializer<>(objectMapper));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CreatePostKafkaEvent> createPostKafkaListenerContainerFactory(
      ConsumerFactory<String, CreatePostKafkaEvent> kafkaConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, CreatePostKafkaEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, SubscriptionChangeKafkaEvent> kafkaSubscriptionChangeConsumerFactory(
      ObjectMapper objectMapper) {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SubscriptionChangeKafkaEvent.class.getName());
    config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
        new JsonDeserializer<>(objectMapper));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, SubscriptionChangeKafkaEvent> subscriptionChangeKafkaListenerContainerFactory(
      ConsumerFactory<String, SubscriptionChangeKafkaEvent> kafkaConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, SubscriptionChangeKafkaEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory);
    return factory;
  }
}
