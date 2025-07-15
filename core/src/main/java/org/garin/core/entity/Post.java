package org.garin.core.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 120, message = "Text must be less than 120 characters")
  private String text;

  @Pattern(regexp = "^#.*", message = "Tag must start with #")
  private String tag;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User author;

  public Post(String text, String tag) {
    this.text = text;
    this.tag = tag;
  }
}
