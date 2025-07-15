package org.garin.core.service;

import org.garin.core.entity.User;
import org.garin.core.event.SubscriptionChangeApplicationEvent;
import org.garin.core.exception.BlabberException;
import org.garin.core.repository.SubscriptionRepositoryImpl;
import org.garin.core.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepositoryImpl userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SubscriptionRepositoryImpl subscriptionRepository;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("testPassword");
  }

  @Test
  void findById_UserExists_ReturnsUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    User result = userService.findById(1L);

    assertNotNull(result);
    assertEquals(testUser.getId(), result.getId());
    assertEquals(testUser.getUsername(), result.getUsername());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void findById_UserNotFound_ThrowsBlabberException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    BlabberException exception =
        assertThrows(BlabberException.class, () -> userService.findById(1L));
    assertEquals("User with id: 1 not found", exception.getMessage());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void create_ValidUser_ReturnsSavedUser() {
    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode("testPassword")).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    User result = userService.create(testUser);

    assertNotNull(result);
    assertEquals(testUser.getId(), result.getId());
    assertEquals(testUser.getUsername(), result.getUsername());
    assertEquals(encodedPassword, testUser.getPassword());
    verify(passwordEncoder, times(1)).encode("testPassword");
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void deleteById_ValidId_DeletesUserAndSubscriptions() {
    when(subscriptionRepository.deleteAllByFollowerIdOrFolloweeId(1L, 1L)).thenReturn(2);

    userService.deleteById(1L);

    verify(subscriptionRepository, times(1)).deleteAllByFollowerIdOrFolloweeId(1L, 1L);
    verify(applicationEventPublisher, times(1))
        .publishEvent(any(SubscriptionChangeApplicationEvent.class));
    verify(userRepository, times(1)).deleteById(1L);
  }
}
