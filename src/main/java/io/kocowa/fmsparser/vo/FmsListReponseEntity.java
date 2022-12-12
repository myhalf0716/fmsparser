package io.kocowa.fmsparser.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsListReponseEntity {

  private String code;
  private String message;

  @JsonProperty("total_count")
  private int totalCount;

  List<FmsContent> objects;
}
