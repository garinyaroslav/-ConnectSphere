package org.garin.core.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

  private Long id;

  private String text;

  private String tag;

  private String author;

}
