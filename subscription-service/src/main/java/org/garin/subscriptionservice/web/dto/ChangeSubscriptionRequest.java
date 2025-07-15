package org.garin.subscriptionservice.web.dto;

import org.garin.subscriptionservice.model.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeSubscriptionRequest {

  @NotNull
  private Long followeeId;

  @NotNull
  private Long followerId;

  @NotNull
  private SubscriptionType subscriptionType;
}
