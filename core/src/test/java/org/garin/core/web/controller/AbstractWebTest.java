package org.garin.core.web.controller;

import org.garin.core.AbstractTest;
import org.garin.core.repository.UserRepository;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

@Transactional
@AutoConfigureMockMvc
@Sql("classpath:db/init_test_data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class AbstractWebTest extends AbstractTest {

  @Autowired
  protected MockMvc MockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected UserRepository userRepository;

  @Value("${app.client.subscription-service.username}")
  protected String clientUser;

  @Value("${app.client.subscription-service.password}")
  protected String password;

  // @RegisterExtension
  // protected static WireMockExtension wireMockExtension =
  // WireMockExtension.newInstance()
  // .options(wireMockConfig().dynamicPort());
}
