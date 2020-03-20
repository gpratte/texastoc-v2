package com.texastoc.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateGamePlayerRequest {
  private int gamePlayerId;
  // TODO game id should move to the url in the controller
  private int gameId;
  private Integer place;
  private boolean knockedOut;
  private boolean roundUpdates;
  private boolean buyInCollected;
  private boolean rebuyAddOnCollected;
  private boolean annualTocCollected;
  private boolean quarterlyTocCollected;
  private Integer chop;
}
