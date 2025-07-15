package org.garin.core.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.garin.core.entity.Post;
import org.garin.core.entity.RoleType;
import org.garin.core.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<User> findById(Long id) {
    String sql =
        "SELECT u.id AS user_id, u.password AS user_password, u.username AS user_username, "
            + "p.id AS post_id, p.tag AS post_tag, p.text AS post_text, "
            + "ur.role AS user_roles_role " + "FROM usr u "
            + "LEFT JOIN post p ON u.id = p.user_id "
            + "LEFT JOIN user_roles ur ON u.id = ur.user_id " + "WHERE u.id = ?";
    List<User> users = jdbcTemplate.query(sql, new UserExtractor(), id);
    return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
  }

  @Override
  public Optional<User> findByUsername(String username) {
    String sql =
        "SELECT u.id AS user_id, u.password AS user_password, u.username AS user_username, "
            + "p.id AS post_id, p.tag AS post_tag, p.text AS post_text, "
            + "ur.role AS user_roles_role " + "FROM usr u "
            + "LEFT JOIN post p ON u.id = p.user_id "
            + "LEFT JOIN user_roles ur ON u.id = ur.user_id " + "WHERE u.username = ?";

    List<User> users = jdbcTemplate.query(sql, new UserExtractor(), username);
    return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
  }

  @Override
  public User save(User user) {
    if (user.getId() == null) {
      String sql = "INSERT INTO usr (password, username) VALUES (?, ?)";
      jdbcTemplate.update(sql, user.getPassword(), user.getUsername());
      user.setId(jdbcTemplate.queryForObject("SELECT LASTVAL()", Long.class));
    } else {
      String sql = "UPDATE usr SET password = ?, username = ? WHERE id = ?";
      jdbcTemplate.update(sql, user.getPassword(), user.getUsername(), user.getId());
    }

    String deleteUserRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
    jdbcTemplate.update(deleteUserRolesSql, user.getId());

    for (RoleType roleType : user.getRoles()) {
      String insertUserRolesSql = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";
      jdbcTemplate.update(insertUserRolesSql, user.getId(), roleType.name());
    }

    return user;
  }

  @Override
  public void deleteById(Long id) {
    String deleteUserRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
    jdbcTemplate.update(deleteUserRolesSql, id);
    String deleteUserSql = "DELETE FROM usr WHERE id = ?";
    jdbcTemplate.update(deleteUserSql, id);
  }

  private static class UserExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
      Map<Long, User> userMap = new HashMap<>();
      Map<Long, Post> postMap = new HashMap<>();

      while (rs.next()) {
        Long userId = rs.getLong("user_id");
        User user = userMap.get(userId);

        if (user == null) {
          user = new User();
          user.setId(userId);
          user.setUsername(rs.getString("user_username"));
          user.setPassword(rs.getString("user_password"));
          user.setRoles(new HashSet<>());
          userMap.put(userId, user);
        }

        String roleString = rs.getString("user_roles_role");
        if (roleString != null && !roleString.isEmpty()) {
          try {
            user.getRoles().add(RoleType.valueOf(roleString));
          } catch (IllegalArgumentException e) {
            System.err.println("Invalid role string: " + roleString);
          }
        }

        Long postId = rs.getLong("post_id");
        if (postId != null && postId != 0) {
          Post post = postMap.get(postId);
          if (post == null) {
            post = new Post();
            post.setId(postId);
            post.setText(rs.getString("post_text"));
            post.setTag(rs.getString("post_tag"));
            post.setAuthor(user);
            user.getPosts().add(post);
            postMap.put(postId, post);
          }
        }
      }

      return new ArrayList<>(userMap.values());
    }
  }

}
