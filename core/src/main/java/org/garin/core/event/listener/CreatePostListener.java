package org.garin.core.event.listener;

import org.garin.core.event.CreatePostApplicationEvent;
import org.garin.core.event.CreatePostKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostListener {

  @Value("${app.kafka.post-topic}")
  private String topicName;

  private final KafkaTemplate<String, CreatePostKafkaEvent> createPostKafkaTemplate;

  @EventListener
  public void onEvent(CreatePostApplicationEvent event) {
    log.info("Get event for create post: {}", event);

    createPostKafkaTemplate.send(topicName,
        new CreatePostKafkaEvent(event.getPostId(), event.getAuthorId(), event.getUsername()));

  }

}
