package org.garin.core.repository;

import java.util.List;
import org.garin.core.entity.Subscription;
import org.garin.core.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository {

  @Query("SELECT s.follower FROM Subscription s WHERE s.followee.id = :followeeId")
  List<User> findFollowersByFolloweeId(Long followeeId);

  boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

  void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

  int deleteAllByFollowerIdOrFolloweeId(Long followerId, Long followeeId);

  Subscription save(Subscription Subscription);
}
