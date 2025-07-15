package org.garin.core.service;

import java.util.List;

import org.garin.core.entity.Subscription;
import org.garin.core.entity.SubscriptionType;
import org.garin.core.entity.User;
import org.garin.core.event.SubscriptionChangeApplicationEvent;
import org.garin.core.exception.BlabberException;
import org.garin.core.repository.SubscriptionRepositoryImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
  private final UserService userService;
  private final SubscriptionRepositoryImpl subscriptionRepository;
  private final ApplicationEventPublisher publisher;

  @Transactional
  public void subscribe(Long followerId, Long followeeId) {
    if (followerId == null || followeeId == null)
      throw new BlabberException("Invalid useranmes provided");

    User follower = userService.findById(followerId);
    User followee = userService.findById(followeeId);

    if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
      Subscription subscription = new Subscription();
      subscription.setFollower(follower);
      subscription.setFollowee(followee);
      subscriptionRepository.save(subscription);
      publisher.publishEvent(new SubscriptionChangeApplicationEvent(this, followeeId, followerId,
          SubscriptionType.SUBSCRIBE));

    }

  }

  @Transactional
  public void unsubscribe(Long followerId, Long followeeId) {
    if (followerId == null || followeeId == null)
      throw new BlabberException("Invalid useranmes provided");

    if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
      log.info("Delete by follower {} and followee {}", followerId, followeeId);
      subscriptionRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
      publisher.publishEvent(new SubscriptionChangeApplicationEvent(this, followeeId, followerId,
          SubscriptionType.UNSUBSCRIBE));
    }

  }

  public List<User> getFollowers(Long followeeId) {
    log.info("Get followers by followeeId: {}", followeeId);
    return subscriptionRepository.findFollowersByFolloweeId(followeeId);
  }

}
