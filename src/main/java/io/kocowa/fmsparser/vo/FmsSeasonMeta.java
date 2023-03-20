package io.kocowa.fmsparser.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FmsSeasonMeta {
  
  public FmsSeasonMeta() {
    this.tags = new ArrayList<>();
  }
  
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

  private List<String> tags;

  private FmsTitle title;
  private FmsSummary summary;
  private FmsDescription description;
}
