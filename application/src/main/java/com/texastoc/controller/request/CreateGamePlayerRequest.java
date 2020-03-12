package com.texastoc.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGamePlayerRequest {
  private int playerId;
  private int gameId;
  private Integer buyInCollected;
  private Integer annualTocCollected;
  private Integer quarterlyTocCollected;
}
