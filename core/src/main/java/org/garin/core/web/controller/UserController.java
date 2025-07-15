package org.garin.core.web.controller;

import org.garin.core.entity.User;
import org.garin.core.service.UserService;
import org.garin.core.web.dto.CreateUserRequest;
import org.garin.core.web.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
    var user = userService.findById(userId);

    return new ResponseEntity<>(new UserDto(user.getId(), user.getUsername()), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest body) {
    var user = userService.create(new User(body.getUsername(), body.getPassword(), body.getRole()));

    return new ResponseEntity<>(new UserDto(user.getId(), user.getUsername()), HttpStatus.CREATED);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
    userService.deleteById(userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
