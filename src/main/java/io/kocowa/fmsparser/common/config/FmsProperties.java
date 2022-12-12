package io.kocowa.fmsparser.common.config;

import java.util.Map;
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

  public static final String API_NAME_SEASON = "season";
  public static final String API_NAME_EPISODE_LIST = "list";

  public static enum ApiName {
    API_NAME_SEASON("season"),
    API_NAME_EPISODE_LIST("list");

    String apiName;

    ApiName(String apiName) {
      this.apiName = apiName;
    }

    public String apiName() {
      return this.apiName;
    }
  }

  private String urlBase;
  private Map<String, String> path;
  private String authorization;
  private String type;
  private String orderBy;
  private String order;
  private int offset;
  private int limit;

  /*
   * @param api : list|season
   */
  public String getUrlString(ApiName apiName) {
    return urlBase.concat("/").concat(path.get(apiName.apiName()));
  }
}
