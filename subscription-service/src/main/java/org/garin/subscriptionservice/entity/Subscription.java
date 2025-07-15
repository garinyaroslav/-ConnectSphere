package org.garin.subscriptionservice.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "subscriptions")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Subscription {

  @Id
  private Long id;

  private Set<Long> subscribersId = new HashSet<>();

  public Subscription(Long id) {
    this.id = id;
  }

}
