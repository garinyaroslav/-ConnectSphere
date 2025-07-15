package org.garin.core.repository;

import java.util.Optional;

import org.garin.core.entity.User;

public interface UserRepository {

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  User save(User user);

  void deleteById(Long id);

}
