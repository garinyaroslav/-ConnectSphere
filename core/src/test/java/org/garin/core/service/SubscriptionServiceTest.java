package org.garin.core.service;

import java.util.List;
import org.garin.core.entity.User;
import org.garin.core.repository.SubscriptionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
  @Mock
  private UserService userService;

  @Mock
  private SubscriptionRepositoryImpl subscriptionRepository;

  @Mock
  private ApplicationEventPublisher publisher;

  @InjectMocks
  private SubscriptionService subscriptionService;

  private User testFollower;
  private User testFollowee;

  @BeforeEach
  public void setUp() {
    testFollower = new User();
    testFollower.setId(1L);
    testFollower.setUsername("follower");

    testFollowee = new User();
    testFollowee.setId(2L);
    testFollowee.setUsername("followee");
  }

  @Test
  public void getFollowers_ValidFolloweeId_ReturnsFollowers() {
    when(subscriptionRepository.findFollowersByFolloweeId(2L)).thenReturn(List.of(testFollower));

    List<User> followers = subscriptionService.getFollowers(2L);

    assertNotNull(followers);
    assertEquals(1, followers.size());
    assertEquals("follower", followers.getFirst().getUsername());
    verify(subscriptionRepository, times(1)).findFollowersByFolloweeId(2L);
  }

  @Test
  public void subscribe_ValidIds_SavesSubscription() {
    when(userService.findById(1L)).thenReturn(testFollower);
    when(userService.findById(2L)).thenReturn(testFollowee);
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

    subscriptionService.subscribe(1L, 2L);

    verify(subscriptionRepository, times(1)).save(any());
    verify(publisher, times(1)).publishEvent(any());
  }

  @Test
  public void subscribe_AlreadySubscribed_DoesNotSave() {
    when(userService.findById(1L)).thenReturn(testFollower);
    when(userService.findById(2L)).thenReturn(testFollowee);
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

    subscriptionService.subscribe(1L, 2L);

    verify(subscriptionRepository, never()).save(any());
    verify(publisher, never()).publishEvent(any());
  }

  @Test
  public void unsubscribe_ValidIds_DeletesSubscription() {
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(true);

    subscriptionService.unsubscribe(1L, 2L);

    verify(subscriptionRepository, times(1)).deleteByFollowerIdAndFolloweeId(1L, 2L);
    verify(publisher, times(1)).publishEvent(any());
  }

  @Test
  public void unsubscribe_NonExistentSubscription_DoesNotDelete() {
    when(subscriptionRepository.existsByFollowerIdAndFolloweeId(1L, 2L)).thenReturn(false);

    subscriptionService.unsubscribe(1L, 2L);

    verify(subscriptionRepository, never()).deleteByFollowerIdAndFolloweeId(1L, 2L);
    verify(publisher, never()).publishEvent(any());
  }
}
