package io.kocowa.fmsparser.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsQueryVO {

  private String parentId;

  private String type;
  private String name;

  private String orderBy;

  private String order;
  private String offset;
  private String limit;

  public FmsQueryVO() {
    type = "media";
    orderBy = "start_date";
    order = "asc";
    offset = "0";
    limit = "1000";
  }
}
