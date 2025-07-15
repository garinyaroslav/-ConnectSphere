package org.garin.core.client;

import org.garin.core.entity.SubscriptionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeSubscriptionRequest {

  private Long followeeId;

  private Long followerId;

  private SubscriptionType subscriptionType;

}
