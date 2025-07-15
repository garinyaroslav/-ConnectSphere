package org.garin.core.client;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeSubscriptionResponse {

  private Long id;

  private Set<Long> subscriberIds = new HashSet<>();

}
