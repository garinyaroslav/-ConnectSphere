package org.garin.core.secutiry;

import org.garin.core.exception.BlabberException;
import org.garin.core.repository.UserRepositoryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepositoryImpl userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username).map(AppUserDetails::new)
        .orElseThrow(() -> new BlabberException("User with username " + username + " not found"));
  }

}
