package org.garin.core.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePostApplicationEvent extends ApplicationEvent {
  private final Long postId;
  private final Long authorId;
  private final String username;

  public CreatePostApplicationEvent(Object source, Long postId, Long authorId, String username) {
    super(source);
    this.postId = postId;
    this.authorId = authorId;
    this.username = username;
  }

}
