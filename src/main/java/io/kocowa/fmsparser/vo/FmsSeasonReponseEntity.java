package io.kocowa.fmsparser.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsSeasonReponseEntity {

  private String code;
  private String message;
  private String duration;

  FmsSeason object;
}
