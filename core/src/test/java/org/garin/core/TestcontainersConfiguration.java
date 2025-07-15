package org.garin.core;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
        .withReuse(true)
        .withUsername("admin")
        .withPassword("admin")
        .withDatabaseName("blabber_db");
  }

}
