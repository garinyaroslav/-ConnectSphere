package org.garin.subscriptionservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

  @Bean
  public UserDetailsService inMemoryUserDetailsService() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(User.withUsername("core_service").password("{noop}12345").roles("CORE_SERVICE").build());

    return manager;
  }

  @Bean
  public AuthenticationManager inMemoryAuthenticationManager(HttpSecurity http,
      UserDetailsService inMemoryUserDetailsService) throws Exception {
    var authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authManagerBuilder.userDetailsService(inMemoryUserDetailsService);

    return authManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
      throws Exception {
    http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS).permitAll()
        .requestMatchers("/css/**", "/ws/**", "/notification/**").permitAll().anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationManager(authenticationManager);

    return http.build();
  }

}
