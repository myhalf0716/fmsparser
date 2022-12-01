package io.kocowa.fmsparser.common.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class PropertyTest {

  @Autowired
  FmsProperties fmsProps;

  @Test
  public void propTest() {
    log.debug(fmsProps.toString());
    assertNotNull(fmsProps.getAuthorization());
  }
}
