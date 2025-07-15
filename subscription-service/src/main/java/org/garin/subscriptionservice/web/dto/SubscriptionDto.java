package org.garin.subscriptionservice.web.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

  private Long id;

  private Set<Long> subscriberIds = new HashSet<>();

}
