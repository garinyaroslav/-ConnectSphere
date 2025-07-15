package org.garin.core.web.controller;

import java.util.List;

import org.garin.core.secutiry.AppUserDetails;
import org.garin.core.service.SubscriptionService;
import org.garin.core.web.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping("/subscribe")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<Void> subscribe(@RequestParam Long followeeId,
      @AuthenticationPrincipal UserDetails userDetails) {
    var followerId = ((AppUserDetails) userDetails).getId();
    subscriptionService.subscribe(followerId, followeeId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/unsubscribe")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<Void> unsubscribe(@RequestParam Long followeeId,
      @AuthenticationPrincipal UserDetails userDetails) {
    var followerId = ((AppUserDetails) userDetails).getId();
    subscriptionService.unsubscribe(followerId, followeeId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/followers")
  public ResponseEntity<List<UserDto>> getFollowers(@RequestParam Long followeeId) {
    List<UserDto> followers = subscriptionService.getFollowers(followeeId).stream()
        .map(obj -> new UserDto(obj.getId(), obj.getUsername())).toList();

    return new ResponseEntity<>(followers, HttpStatus.OK);
  }
}
