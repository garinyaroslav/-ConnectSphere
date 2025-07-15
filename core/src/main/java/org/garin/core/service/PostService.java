package org.garin.core.service;

import org.garin.core.entity.Post;
import org.garin.core.event.CreatePostApplicationEvent;
import org.garin.core.exception.BlabberException;
import org.garin.core.repository.PostRepositoryImpl;
import org.garin.core.repository.specification.PostFilter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
  private final PostRepositoryImpl postRepository;
  private final UserService userService;
  private final ApplicationEventPublisher publisher;

  public Page<Post> findAll(Pageable pageable) {
    log.info("Find all by pageable: {}", pageable);
    return postRepository.findAll(pageable);
  }

  public Page<Post> filter(PostFilter filter, Pageable pageable) {
    log.info("Filter posts by data: {}", filter);
    return postRepository.findAll(filter, pageable);
  }

  @Transactional
  public Post create(Post post, Long authorId) {
    log.info("Create new post: {}", post);
    var author = userService.findById(authorId);
    post.setAuthor(author);

    var newPost = postRepository.save(post);

    publisher.publishEvent(
        new CreatePostApplicationEvent(this, newPost.getId(), authorId, author.getUsername()));

    return newPost;
  }

  @Transactional
  public void deleteById(Long postId, Long userId) {
    log.info("Delete post by id: {}", postId);

    if (!postRepository.existsByAuthorIdAndId(userId, postId)) {
      log.error("Exception on delete. PostId: {}, UserId: {}", postId, userId);
      throw new BlabberException("Exception trying to delete post with id: " + postId);
    }

    postRepository.deleteById(postId);
  }

}
