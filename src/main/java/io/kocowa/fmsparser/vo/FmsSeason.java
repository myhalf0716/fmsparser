package io.kocowa.fmsparser.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsSeason {

  private String id;

  @JsonProperty("parent_id")
  private String preantId;

  private String type;
  private FmsSeasonMeta meta;

  @JsonProperty("media_id")
  private String mediaId;
}
