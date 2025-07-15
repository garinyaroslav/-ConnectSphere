package org.garin.core.repository.specification;

import org.garin.core.entity.Post;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;

public interface PostSpecification {

  static Specification<Post> withFilter(PostFilter filter) {
    return Specification.where(isEquals(filter.getAuthorId(), "author", "id"))
        .and(contains("tag", filter.getTag()))
        .and(contains("text", filter.getText()));
  }

  private static <T> Specification<Post> isEquals(T object, String... fieldPath) {
    return ((root, query, criteriaBuilder) -> {
      if (object == null)
        return criteriaBuilder.conjunction();

      Path<?> rootByPath = root;
      for (String filed : fieldPath) {
        rootByPath = rootByPath.get(filed);
      }

      return criteriaBuilder.equal(rootByPath, object);
    });
  }

  private static <T> Specification<Post> contains(String fieldName, String keyword) {
    return ((root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.isBlank())
        return criteriaBuilder.conjunction();

      return criteriaBuilder.like(root.get(fieldName), "%" + keyword + "%");
    });
  }

}
