package org.garin.subscriptionservice.model;

import lombok.Data;

@Data
public class CreatePostKafkaEvent {

  private Long postId;

  private Long authorId;

  private String username;

}
