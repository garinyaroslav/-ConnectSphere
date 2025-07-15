package org.garin.subscriptionservice.listener;

import org.garin.subscriptionservice.exception.BlabberException;
import org.garin.subscriptionservice.model.SubscriptionChangeKafkaEvent;
import org.garin.subscriptionservice.service.SubscriptionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionChangeKafkaListener {

  private final SubscriptionService subscriptionService;

  @KafkaListener(topics = "${app.kafka.subscribtion-topic}", groupId = "${app.kafka.groupId}",
      containerFactory = "subscriptionChangeKafkaListenerContainerFactory")
  public void listen(@Payload SubscriptionChangeKafkaEvent event) {
    switch (event.getSubscriptionType()) {
      case SUBSCRIBE:
        log.info("Change request for change subscription: {}", event);
        subscriptionService.addSubscriber(event.getFolloweeId(), event.getFollowerId());
        break;
      case UNSUBSCRIBE:
        log.info("Change request for change subscription: {}", event);
        subscriptionService.removeSubscriber(event.getFolloweeId(), event.getFollowerId());
        break;
      case REMOVE:
        Long id = event.getFolloweeId();
        log.info("Delete subscription: {}", id);
        subscriptionService.deleteSubscriptionById(id);
        break;
      default:
        throw new BlabberException("Unknown subscription type: " + event.getSubscriptionType());
    }
  }

}
