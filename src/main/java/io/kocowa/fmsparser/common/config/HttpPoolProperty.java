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
@ConfigurationProperties(prefix = "http.pool")
public class HttpPoolProperty {

  private Integer maxTotal;
  private Integer defaultMaxPerRoute;
  private Integer connectionTimeout;
  private Integer connectionRequestTimeout;
  private Integer readTimeout;
  private Integer validateAfterInactivity;
}
