package org.garin.subscriptionservice.service;

import java.util.Set;

import org.garin.subscriptionservice.entity.Subscription;
import org.garin.subscriptionservice.exception.BlabberException;
import org.garin.subscriptionservice.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;

  public Subscription getSubscriptionById(Long id) {
    return subscriptionRepository.findById(id)
        .orElseThrow(() -> new BlabberException("Subscription with id " + id + " not found"));
  }

  public Subscription addSubscriber(Long id, Long subscriberId) {
    log.info("Add subscriber {} in subscription {}", subscriberId, id);

    Subscription subscription = subscriptionRepository.findById(id).orElse(new Subscription(id));

    subscription.getSubscribersId().add(subscriberId);

    return subscriptionRepository.save(subscription);
  }

  public Subscription removeSubscriber(Long id, Long subscriberId) {
    log.info("Remove subscriber {} from subscription {}", subscriberId, id);

    Subscription subscription = getSubscriptionById(id);

    subscription.getSubscribersId().remove(subscriberId);

    return subscriptionRepository.save(subscription);
  }

  public void deleteSubscriptionById(Long id) {
    log.info("Delete subscription {}", id);

    var subscriptions = subscriptionRepository.findAllBySubscribersIdIn(Set.of(id));

    subscriptions.forEach(it -> it.getSubscribersId().remove(id));

    log.info("Updated subscription: {}", subscriptions);

    subscriptionRepository.saveAll(subscriptions);

    subscriptionRepository.deleteById(id);
  }

}
