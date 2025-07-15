package org.garin.core.service;

import org.garin.core.entity.SubscriptionType;
import org.garin.core.entity.User;
import org.garin.core.event.SubscriptionChangeApplicationEvent;
import org.garin.core.exception.BlabberException;
import org.garin.core.repository.SubscriptionRepositoryImpl;
import org.garin.core.repository.UserRepositoryImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepositoryImpl userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SubscriptionRepositoryImpl subscriptionRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public User findById(Long userId) {
    log.info("Get user by id: {}", userId);
    return userRepository.findById(userId)
        .orElseThrow(() -> new BlabberException("User with id: " + userId + " not found"));
  }

  public User create(User user) {
    log.info("Create user with username: {}", user.getUsername());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Transactional
  public void deleteById(Long userId) {
    log.info("Delete user by id: {}", userId);
    int countOfDelete = subscriptionRepository.deleteAllByFollowerIdOrFolloweeId(userId, userId);
    log.info("Deleted subscriptions count: {}", countOfDelete);

    applicationEventPublisher.publishEvent(
        new SubscriptionChangeApplicationEvent(this, userId, userId, SubscriptionType.REMOVE));

    userRepository.deleteById(userId);
  }

}
