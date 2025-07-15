package org.garin.subscriptionservice.web.controller;

import org.garin.subscriptionservice.model.SubscriptionType;
import org.garin.subscriptionservice.service.SubscriptionService;
import org.garin.subscriptionservice.web.dto.ChangeSubscriptionRequest;
import org.garin.subscriptionservice.web.dto.SubscriptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_CORE_SERVICE')")
  public ResponseEntity<SubscriptionDto> changeSubscription(
      @RequestBody ChangeSubscriptionRequest request) {
    log.info("Change request for change subscription: {}", request);

    if (request.getSubscriptionType() == SubscriptionType.SUBSCRIBE) {
      subscriptionService.addSubscriber(request.getFolloweeId(), request.getFollowerId());
    } else {
      subscriptionService.removeSubscriber(request.getFolloweeId(), request.getFollowerId());
    }

    var updatedSubscription = subscriptionService.getSubscriptionById(request.getFolloweeId());

    return new ResponseEntity<>(
        new SubscriptionDto(updatedSubscription.getId(), updatedSubscription.getSubscribersId()),
        HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_CORE_SERVICE')")
  public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
    log.info("Delete subscription: {}", id);
    subscriptionService.deleteSubscriptionById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
