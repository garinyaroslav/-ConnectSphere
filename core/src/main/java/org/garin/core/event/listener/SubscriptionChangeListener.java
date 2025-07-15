package org.garin.core.event.listener;

import org.garin.core.event.SubscriptionChangeApplicationEvent;
import org.garin.core.event.SubscriptionChangeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionChangeListener {
  @Value("${app.kafka.subscribtion-topic}")
  private String topicName;

  private final KafkaTemplate<String, SubscriptionChangeKafkaEvent> subscriptionChangeKafkaTemplate;

  @EventListener
  public void onEvent(SubscriptionChangeApplicationEvent event) {
    log.info("Get event for subscription change: {}", event);

    subscriptionChangeKafkaTemplate.send(topicName, new SubscriptionChangeKafkaEvent(
        event.getFolloweeId(), event.getFollowerId(), event.getSubscriptionType()));
  }

}
