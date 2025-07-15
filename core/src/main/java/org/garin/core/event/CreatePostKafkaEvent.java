package org.garin.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostKafkaEvent {

  private Long postId;

  private Long authorId;

  private String username;

}
