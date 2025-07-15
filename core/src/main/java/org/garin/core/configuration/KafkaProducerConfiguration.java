package org.garin.core.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.garin.core.event.CreatePostKafkaEvent;
import org.garin.core.event.SubscriptionChangeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaProducerConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, CreatePostKafkaEvent> kafkaCreatePostProducerFactory(
      ObjectMapper objectMapper) {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return new DefaultKafkaProducerFactory<>(config, new StringSerializer(),
        new JsonSerializer<>(objectMapper));
  }

  @Bean
  public ProducerFactory<String, SubscriptionChangeKafkaEvent> kafkaSubscriptionChangeProducerFactory(
      ObjectMapper objectMapper) {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return new DefaultKafkaProducerFactory<>(config, new StringSerializer(),
        new JsonSerializer<>(objectMapper));
  }

  @Bean
  public KafkaTemplate<String, CreatePostKafkaEvent> createPostKafkaTemplate(
      ProducerFactory<String, CreatePostKafkaEvent> kafkaCreatePostProducerFactory) {
    return new KafkaTemplate<>(kafkaCreatePostProducerFactory);
  }

  @Bean
  public KafkaTemplate<String, SubscriptionChangeKafkaEvent> subscriptionChangeKafkaTemplate(
      ProducerFactory<String, SubscriptionChangeKafkaEvent> kafkaSubscriptionChangeProducerFactory) {
    return new KafkaTemplate<>(kafkaSubscriptionChangeProducerFactory);
  }


}
