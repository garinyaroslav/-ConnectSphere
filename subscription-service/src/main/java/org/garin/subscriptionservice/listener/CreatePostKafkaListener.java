package org.garin.subscriptionservice.listener;

import org.garin.subscriptionservice.model.CreatePostKafkaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePostKafkaListener {

  @KafkaListener(topics = "${app.kafka.post-topic}", groupId = "${app.kafka.groupId}",
      containerFactory = "createPostKafkaListenerContainerFactory")
  public void listen(@Payload CreatePostKafkaEvent event) {
    log.info("Received create post event: {}", event);
  }

}
