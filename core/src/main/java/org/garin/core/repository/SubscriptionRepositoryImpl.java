package org.garin.core.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.garin.core.entity.Subscription;
import org.garin.core.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<User> findFollowersByFolloweeId(Long followeeId) {
    String sql =
        "SELECT u.* FROM usr u JOIN subscription s ON u.id = s.follower_id WHERE s.followee_id = ?";
    return jdbcTemplate.query(sql, new UserExtractor(), followeeId);
  }

  @Override
  public boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
    String sql = "SELECT COUNT(*) FROM subscription WHERE follower_id = ? AND followee_id = ?";
    Integer count =
        jdbcTemplate.queryForObject(sql, Integer.class, new Object[] {followerId, followeeId});
    return count != null && count > 0;
  }

  @Override
  public void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
    String sql = "DELETE FROM subscription WHERE follower_id = ? AND followee_id = ?";
    jdbcTemplate.update(sql, followerId, followeeId);
  }

  @Override
  public int deleteAllByFollowerIdOrFolloweeId(Long followerId, Long followeeId) {
    String sql = "DELETE FROM subscription WHERE follower_id = ? OR followee_id = ?";
    return jdbcTemplate.update(sql, followerId, followeeId);
  }

  @Override
  public Subscription save(Subscription subscription) {
    String sql = "INSERT INTO subscription (follower_id, followee_id) VALUES (?, ?) RETURNING id";
    Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
        subscription.getFollower().getId(), subscription.getFollowee().getId());

    subscription.setId(generatedId);
    return subscription;
  }

  private static class UserExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
      ArrayList<User> users = new ArrayList<>();
      while (rs.next()) {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        users.add(user);
      }
      return users;
    }

  }
}
