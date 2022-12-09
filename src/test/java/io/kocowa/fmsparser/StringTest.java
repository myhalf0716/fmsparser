package io.kocowa.fmsparser;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class StringTest {

  @Test
  public void splitTest() {
    String source = "1.2.3";
    String[] ids = source.split("\\.");

    for (String id : ids) {
      log.info("ID [{}]", id);
    }

    assert (ids.length == 3);
  }
}
