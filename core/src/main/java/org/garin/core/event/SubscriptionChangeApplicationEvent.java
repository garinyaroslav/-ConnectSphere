package org.garin.core.event;

import org.garin.core.entity.SubscriptionType;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SubscriptionChangeApplicationEvent extends ApplicationEvent {

  private final Long followeeId;

  private final Long followerId;

  private final SubscriptionType subscriptionType;

  public SubscriptionChangeApplicationEvent(Object source, Long followeeId, Long followerId,
      SubscriptionType subscriptionType) {
    super(source);

    this.followeeId = followeeId;
    this.followerId = followerId;
    this.subscriptionType = subscriptionType;

  }

}
