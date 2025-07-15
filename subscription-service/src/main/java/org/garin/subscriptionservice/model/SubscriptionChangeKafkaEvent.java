package org.garin.subscriptionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionChangeKafkaEvent {

  private Long followeeId;

  private Long followerId;

  private SubscriptionType subscriptionType;

}
