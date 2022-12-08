package io.kocowa.fmsparser.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsContent {

  @Getter
  @Setter
  @ToString
  public static class FmsMetaItemBase {

    private String ko;
    private String en;
    private String es;
    private String pt;
  }

  @Getter
  @Setter
  @ToString(callSuper = true)
  public static class FmsDescription extends FmsMetaItemBase {}

  @Getter
  @Setter
  @ToString(callSuper = true)
  public static class FmsSummary extends FmsMetaItemBase {}

  @Getter
  @Setter
  @ToString(callSuper = true)
  public static class FmsTitle extends FmsMetaItemBase {}

  @Getter
  @Setter
  @ToString
  public static class FmsMeta {

    private FmsTitle title;
    private FmsSummary summary;
    private FmsDescription description;
  }

  private String id;

  @JsonProperty("parent_id")
  private String preantId;

  private String type;
  private FmsMeta meta;

  @JsonProperty("season_number")
  private int seasonNumber;

  @JsonProperty("episode_number")
  private int episodeNumber;
}
