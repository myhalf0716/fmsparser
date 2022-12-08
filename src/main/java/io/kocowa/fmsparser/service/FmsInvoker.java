package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.common.config.FmsProperties;
import io.kocowa.fmsparser.vo.FmsContent;
import io.kocowa.fmsparser.vo.FmsQueryVO;
import io.kocowa.fmsparser.vo.FmsReponseEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FmsInvoker implements HttpInvoker {

  private final FmsProperties fmsProps;

  private final RestTemplate restTemplate;

  @Override
  public FmsReponseEntity invoke(FmsQueryVO query) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", fmsProps.getAuthorization());

    StringBuilder sb = new StringBuilder(fmsProps.getUrlString())
      .append("?parent_id=")
      .append(query.getParentId())
      .append("&type=")
      .append(query.getType())
      .append("&order_by=")
      .append(query.getOrderBy())
      .append("&order=")
      .append(query.getOrder())
      .append("&offset=")
      .append(query.getOffset())
      .append("&limit=")
      .append(query.getLimit());

    String uri = sb.toString();

    HttpEntity request = new HttpEntity(headers);

    ResponseEntity<FmsReponseEntity> res = restTemplate.exchange(
      uri,
      HttpMethod.GET,
      request,
      // (Class<List<FmsContent>>) (Object) List.class
      FmsReponseEntity.class
    );

    FmsReponseEntity body = res.getBody();
    log.debug("body [{}]", body);

    return body;
  }
}
