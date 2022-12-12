package io.kocowa.fmsparser;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;
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

  @Test
  public void StringListTest() {
    List<String> list = Arrays.asList("AAA", "BBB");
    log.info("list >>{}", list);
  }

  @Test
  public void bracketTest() {
    String before = "[meta]";
    log.info("before [{}]", before);

    String after = before.replaceAll("\\[", "").replaceAll("]", "");
    log.info("after [{}]", after);

    assertNotEquals(before, after);
  }
}
