package io.kocowa.fmsparser.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HttpPoolProperty {

  private Integer maxTotal;
  private Integer defaultMaxPerRoute;
  private Integer connectionTimeout;
  private Integer connectionRequestTimeout;
  private Integer readTimeout;
  private Integer validateAfterInactivity;
}
