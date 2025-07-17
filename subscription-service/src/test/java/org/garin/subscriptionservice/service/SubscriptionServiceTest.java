package org.garin.subscriptionservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import org.garin.subscriptionservice.entity.Subscription;
import org.garin.subscriptionservice.exception.BlabberException;
import org.garin.subscriptionservice.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @InjectMocks
  private SubscriptionService subscriptionService;

  private Subscription testSubscription;

  @BeforeEach
  public void setUp() {
    testSubscription = new Subscription(1L);
    testSubscription.setSubscribersId(new HashSet<>(Set.of(2L)));
  }

  @Test
  public void getSubscriptionById_ValidId_ReturnsSubscription() {
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

    Subscription result = subscriptionService.getSubscriptionById(1L);

    assertNotNull(result);
    assertEquals(testSubscription.getId(), result.getId());
    assertEquals(testSubscription.getSubscribersId(), result.getSubscribersId());

    verify(subscriptionRepository, times(1)).findById(1L);
  }

  @Test
  public void getSubscriptionById_InvalidId_ThrowsBlabberException() {
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(BlabberException.class, () -> subscriptionService.getSubscriptionById(1L));
    assertEquals("Subscription with id " + 1L + " not found", exception.getMessage());

    verify(subscriptionRepository, times(1)).findById(1L);
  }

  @Test
  public void addSubscriber_ExistingSubscription_AddsSubscriberAndSaves() {
    Long subscriberId = 3L;
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
    when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

    Subscription result = subscriptionService.addSubscriber(1L, subscriberId);

    assertNotNull(result);
    assertTrue(result.getSubscribersId().contains(subscriberId));
    assertEquals(2, result.getSubscribersId().size());
    verify(subscriptionRepository, times(1)).findById(1L);
    verify(subscriptionRepository, times(1)).save(testSubscription);
  }

  @Test
  public void addSubscriber_NewSubscription_CreatesAndAddsSubscriber() {
    Long subscriberId = 3L;
    Subscription newSubscription = new Subscription(1L);
    newSubscription.setSubscribersId(new HashSet<>(Set.of(subscriberId)));
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());
    when(subscriptionRepository.save(any(Subscription.class))).thenReturn(newSubscription);

    Subscription result = subscriptionService.addSubscriber(1L, subscriberId);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertTrue(result.getSubscribersId().contains(subscriberId));
    assertEquals(1, result.getSubscribersId().size());
    verify(subscriptionRepository, times(1)).findById(1L);
    verify(subscriptionRepository, times(1)).save(any(Subscription.class));
  }

  @Test
  public void removeSubscriber_ValidSubscriptionAndSubscriber_RemovesSubscriberAndSaves() {
    Long subscriberId = 2L;
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
    when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

    Subscription result = subscriptionService.removeSubscriber(1L, subscriberId);

    assertNotNull(result);
    assertFalse(result.getSubscribersId().contains(subscriberId));
    assertEquals(0, result.getSubscribersId().size());
    verify(subscriptionRepository, times(1)).findById(1L);
    verify(subscriptionRepository, times(1)).save(testSubscription);
  }

  @Test
  public void removeSubscriber_SubscriptionNotFound_ThrowsBlabberException() {
    Long subscriberId = 2L;
    when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(BlabberException.class,
        () -> subscriptionService.removeSubscriber(1L, subscriberId));
    assertEquals("Subscription with id " + 1L + " not found", exception.getMessage());

    verify(subscriptionRepository, times(1)).findById(1L);
    verify(subscriptionRepository, never()).save(any(Subscription.class));
  }

  @Test
  public void deleteSubscriptionById_ValidId_DeletesSubscriptionAndUpdatesOthers() {
    Long id = 1L;
    Subscription otherSubscription = new Subscription(2L);
    otherSubscription.setSubscribersId(new HashSet<>(Set.of(1L, 3L)));
    List<Subscription> subscriptions = List.of(otherSubscription);
    when(subscriptionRepository.findAllBySubscribersIdIn(Set.of(id))).thenReturn(subscriptions);
    when(subscriptionRepository.saveAll(any())).thenReturn(subscriptions);

    subscriptionService.deleteSubscriptionById(id);

    assertFalse(otherSubscription.getSubscribersId().contains(id));
    verify(subscriptionRepository, times(1)).findAllBySubscribersIdIn(Set.of(id));
    verify(subscriptionRepository, times(1)).saveAll(subscriptions);
    verify(subscriptionRepository, times(1)).deleteById(id);
  }

  @Test
  public void deleteSubscriptionById_NoRelatedSubscriptions_DeletesSubscription() {
    Long id = 1L;
    when(subscriptionRepository.findAllBySubscribersIdIn(Set.of(id))).thenReturn(List.of());

    subscriptionService.deleteSubscriptionById(id);

    verify(subscriptionRepository, times(1)).findAllBySubscribersIdIn(Set.of(id));
    verify(subscriptionRepository, times(1)).saveAll(any());
    verify(subscriptionRepository, times(1)).deleteById(id);
  }
}
