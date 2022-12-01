package io.kocowa.fmsparser.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "http")
public class RestTemplateConfiguration {

  HttpPoolProperty pool;
}
