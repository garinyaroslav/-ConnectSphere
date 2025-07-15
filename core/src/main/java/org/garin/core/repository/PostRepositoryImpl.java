package org.garin.core.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.garin.core.entity.Post;
import org.garin.core.entity.User;
import org.garin.core.repository.specification.PostFilter;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Page<Post> findAll(PostFilter postFilter, Pageable pageable) {
    List<Object> params = new ArrayList<>();
    List<Object> countParams = new ArrayList<>();

    StringBuilder sql =
        new StringBuilder("SELECT p.id AS post_id, p.tag AS post_tag, p.text AS post_text, "
            + "p.user_id AS post_user_id, u.username AS user_username "
            + "FROM post p JOIN usr u ON p.user_id = u.id");

    StringBuilder whereClause = new StringBuilder();
    boolean hasFilter = false;

    if (postFilter.getAuthorId() != null) {
      whereClause.append(" AND p.user_id = ?");
      params.add(postFilter.getAuthorId());
      countParams.add(postFilter.getAuthorId());
      hasFilter = true;
    }
    if (postFilter.getTag() != null && !postFilter.getTag().isBlank()) {
      whereClause.append(" AND p.tag LIKE ?");
      params.add("%" + postFilter.getTag() + "%");
      countParams.add("%" + postFilter.getTag() + "%");
      hasFilter = true;
    }
    if (postFilter.getText() != null && !postFilter.getText().isBlank()) {
      whereClause.append(" AND p.text LIKE ?");
      params.add("%" + postFilter.getText() + "%");
      countParams.add("%" + postFilter.getText() + "%");
      hasFilter = true;
    }

    if (hasFilter) {
      sql.append(" WHERE ").append(whereClause.substring(5));
    }

    sql.append(" LIMIT ? OFFSET ?");
    params.add(pageable.getPageSize());
    params.add(pageable.getOffset());

    String countSql = "SELECT COUNT(*) FROM post p";
    if (hasFilter) {
      countSql += " WHERE " + whereClause.substring(5);
    }

    Long total = jdbcTemplate.queryForObject(countSql, Long.class, countParams.toArray());

    List<Post> posts = jdbcTemplate.query(sql.toString(), new PostExtractor(), params.toArray());
    return new PageImpl<>(posts, pageable, total != null ? total : 0);
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    String sql = "SELECT p.id AS post_id, p.tag AS post_tag, p.text AS post_text, "
        + "p.user_id AS post_user_id, u.username AS user_username "
        + "FROM post p JOIN usr u ON p.user_id = u.id " + "ORDER BY p.id DESC LIMIT ? OFFSET ?";

    List<Post> posts =
        jdbcTemplate.query(sql, new PostExtractor(), pageable.getPageSize(), pageable.getOffset());

    String countSql = "SELECT COUNT(*) FROM post";
    Long total = jdbcTemplate.queryForObject(countSql, Long.class);
    System.out.println("Total posts: " + total);

    return new PageImpl<>(posts, pageable, total != null ? total : 0);
  }

  @Override
  public Optional<Post> findById(Long id) {
    String sql = "SELECT p.id AS post_id, p.tag AS post_tag, p.text AS post_text, "
        + "p.user_id AS post_user_id, u.username AS user_username "
        + "FROM post p JOIN usr u ON p.user_id = u.id WHERE p.id = ?";

    List<Post> posts = jdbcTemplate.query(sql, new PostExtractor(), id);

    return posts.isEmpty() ? Optional.empty() : Optional.of(posts.getFirst());
  }

  @Override
  public Post save(Post post) {
    if (post.getId() == null) {
      String sql = "INSERT INTO post (user_id, tag, text) VALUES (?, ?, ?) RETURNING id";

      Long id = jdbcTemplate.queryForObject(sql, Long.class,
          new Object[] {post.getAuthor().getId(), post.getTag(), post.getText()});

      post.setId(id);
    } else {
      String sql = "UPDATE post SET user_id = ?, tag = ?, text = ? WHERE id = ?";

      jdbcTemplate.update(sql, post.getAuthor().getId(), post.getTag(), post.getText(),
          post.getId());
    }
    return post;
  }

  @Override
  public void deleteById(Long id) {
    String sql = "DELETE FROM post WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  @Override
  public boolean existsByAuthorIdAndId(Long userId, Long postId) {
    String sql = "SELECT COUNT(*) FROM post WHERE id = ? AND user_id = ?";

    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[] {postId, userId});

    return count != null && count > 0;
  }

  private static class PostExtractor implements ResultSetExtractor<List<Post>> {
    @Override
    public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
      Map<Long, Post> postMap = new HashMap<>();

      while (rs.next()) {
        Long postId = rs.getLong("post_id");
        Post post = postMap.get(postId);

        if (post == null) {
          post = new Post();
          post.setId(postId);
          post.setText(rs.getString("post_text"));
          post.setTag(rs.getString("post_tag"));

          User author = new User();
          author.setId(rs.getLong("post_user_id"));
          author.setUsername(rs.getString("user_username"));
          post.setAuthor(author);

          postMap.put(postId, post);
        }
      }

      return new ArrayList<>(postMap.values());
    }
  }
}
