package org.garin.core.web.dto;

import org.garin.core.entity.RoleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

  private String username;

  private String password;

  private RoleType role;

}
