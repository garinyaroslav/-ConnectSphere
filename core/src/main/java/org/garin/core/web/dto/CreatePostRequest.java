package org.garin.core.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

  @Size(max = 120, message = "Title must be less than 120 characters")
  private String text;

  @Pattern(regexp = "^#.*", message = "Tag must start with #")
  private String tag;

}
