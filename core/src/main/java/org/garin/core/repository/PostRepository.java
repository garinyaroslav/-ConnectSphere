package org.garin.core.repository;

import java.util.Optional;
import org.garin.core.entity.Post;
import org.garin.core.repository.specification.PostFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

  Page<Post> findAll(PostFilter postFilter, Pageable pageable);

  Page<Post> findAll(Pageable pageable);

  Optional<Post> findById(Long id);

  Post save(Post user);

  void deleteById(Long id);

  boolean existsByAuthorIdAndId(Long userId, Long postId);
}
