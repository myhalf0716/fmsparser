package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.common.config.FmsProperties;
import io.kocowa.fmsparser.common.config.FmsProperties.ApiName;
import io.kocowa.fmsparser.vo.FmsQueryVO;
import io.kocowa.fmsparser.vo.FmsSeasonReponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FmsSeasonInvoker implements HttpInvoker {

  private final FmsProperties fmsProps;

  private final RestTemplate restTemplate;

  @Override
  public FmsSeasonReponseEntity invoke(FmsQueryVO query) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", fmsProps.getAuthorization());

    StringBuilder sb = new StringBuilder(
      fmsProps.getUrlString(ApiName.API_NAME_SEASON)
    )
      .append("?id=")
      .append(query.getParentId());
    String uri = sb.toString();

    HttpEntity request = new HttpEntity(headers);

    ResponseEntity<FmsSeasonReponseEntity> res = restTemplate.exchange(
      uri,
      HttpMethod.GET,
      request,
      FmsSeasonReponseEntity.class
    );

    FmsSeasonReponseEntity body = res.getBody();
    // log.debug("body [{}]", body);

    return body;
  }
}
