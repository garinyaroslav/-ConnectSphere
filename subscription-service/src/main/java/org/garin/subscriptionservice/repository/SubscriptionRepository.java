package org.garin.subscriptionservice.repository;

import java.util.Collection;
import java.util.List;

import org.garin.subscriptionservice.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<Subscription, Long> {
  List<Subscription> findAllBySubscribersIdIn(Collection<Long> subscriberIds);
}
