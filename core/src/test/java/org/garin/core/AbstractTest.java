package org.garin.core;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Import({ TestcontainersConfiguration.class, TestConfiguration.class })
public abstract class AbstractTest {

  protected static final KafkaContainer KAFKA = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:7.3.3")).withReuse(true);

  @BeforeAll
  public static void beforeAll() {
    KAFKA.start();

    System.setProperty("spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers());
  }
}
