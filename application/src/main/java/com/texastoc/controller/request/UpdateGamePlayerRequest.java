package com.texastoc.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateGamePlayerRequest {
  private int playerId;
  private Integer place;
  private Boolean knockedOut;
  private Boolean roundUpdates;
  private Integer buyInCollected;
  private Integer rebuyAddOnCollected;
  private Integer annualTocCollected;
  private Integer quarterlyTocCollected;
  private Integer chop;
}
