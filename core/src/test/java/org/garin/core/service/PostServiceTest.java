package org.garin.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import org.garin.core.entity.Post;
import org.garin.core.entity.User;
import org.garin.core.exception.BlabberException;
import org.garin.core.repository.PostRepositoryImpl;
import org.garin.core.repository.specification.PostFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @Mock
  private PostRepositoryImpl postRepository;

  @Mock
  private UserService userService;

  @Mock
  private ApplicationEventPublisher publisher;

  @InjectMocks
  private PostService postService;

  private Pageable pageable;
  private Post testPost;
  private User testUser;
  private Page<Post> testPage;
  private PostFilter filter;

  @BeforeEach
  public void setUp() {
    pageable = PageRequest.of(0, 10);

    testPost = new Post();
    testPost.setId(1L);
    testPost.setText("Test Content");
    testPost.setTag("#test-tag");
    testUser = new User();
    testUser.setId(1L);
    testPost.setAuthor(testUser);

    testPage = new PageImpl<>(Collections.singletonList(testPost), pageable, 1);

    filter = new PostFilter();
  }

  @Test
  public void findAll_ValidPageable_ReturnsPostPage() {
    when(postRepository.findAll(pageable)).thenReturn(testPage);

    Page<Post> result = postService.findAll(pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(testPost.getId(), result.getContent().getFirst().getId());
    assertEquals(testPost.getText(), result.getContent().getFirst().getText());
    assertEquals(testPost.getTag(), result.getContent().getFirst().getTag());
    assertEquals(testPost.getAuthor().getId(), result.getContent().getFirst().getAuthor().getId());

    verify(postRepository, times(1)).findAll(pageable);
  }

  @Test
  public void filter_ValidFilterAndPageable_ReturnsFilteredPostPage() {
    when(postRepository.findAll(filter, pageable)).thenReturn(testPage);

    Page<Post> result = postService.filter(filter, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(testPost.getId(), result.getContent().getFirst().getId());
    assertEquals(testPost.getText(), result.getContent().getFirst().getText());
    assertEquals(testPost.getTag(), result.getContent().getFirst().getTag());

    verify(postRepository, times(1)).findAll(filter, pageable);
  }

  @Test
  public void create_ValidPostAndAuthorId_ReturnsCreatedPost() {
    when(userService.findById(1L)).thenReturn(testUser);
    when(postRepository.save(testPost)).thenReturn(testPost);

    Post result = postService.create(testPost, 1L);

    assertNotNull(result);
    assertEquals(testPost.getId(), result.getId());
    assertEquals(testPost.getText(), result.getText());
    assertEquals(testPost.getTag(), result.getTag());
    assertEquals(testUser.getId(), result.getAuthor().getId());

    verify(userService, times(1)).findById(1L);
    verify(postRepository, times(1)).save(testPost);
    verify(publisher, times(1)).publishEvent(any());
  }

  @Test
  public void create_ValidPostAndNotExistendAuthor_ThrowsBlabberException() {
    when(userService.findById(1L))
        .thenThrow(new BlabberException("User with id: " + testUser.getId() + " not found"));

    BlabberException exception =
        assertThrows(BlabberException.class, () -> postService.create(testPost, 1L));
    assertEquals("User with id: " + testUser.getId() + " not found", exception.getMessage());

    verify(userService, times(1)).findById(1L);
    verify(postRepository, never()).save(testPost);
    verify(publisher, never()).publishEvent(any());
  }

  @Test
  public void deleteById_ValidPostIdAndUserId_DeletesPost() {
    when(postRepository.existsByAuthorIdAndId(1L, 1L)).thenReturn(true);

    postService.deleteById(1L, 1L);

    verify(postRepository, times(1)).existsByAuthorIdAndId(1L, 1L);
    verify(postRepository, times(1)).deleteById(1L);
  }

  @Test
  public void deleteById_InvalidPostIdAndUserId_ThrowsBlabberException() {
    when(postRepository.existsByAuthorIdAndId(1L, 1L)).thenReturn(false);

    BlabberException exception =
        assertThrows(BlabberException.class, () -> postService.deleteById(1L, 1L));
    assertEquals("Exception trying to delete post with id: " + 1L, exception.getMessage());

    verify(postRepository, times(1)).existsByAuthorIdAndId(1L, 1L);
    verify(postRepository, never()).deleteById(1L);
  }


}
