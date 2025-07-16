package org.garin.core.repository.specification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostFilter {

  private String tag;

  private Long authorId;

  private String text;

  private Integer pageSize = 10;

  private Integer pageNumber = 0;

}
