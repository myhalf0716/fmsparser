package io.kocowa.fmsparser.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "fms")
public class FmsProperties {

  private String urlBase;
  private String path;
  private String authorization;
  private String type;
  private String orderBy;
  private String order;
  private int offset;
  private int limit;
}
