package com.texastoc.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayer implements Comparable<GamePlayer> {

  private int id;
  private int playerId;
  private int qSeasonId;
  private int seasonId;
  private int gameId;
  private String name;
  private Integer points;
  private Integer place;
  private Boolean knockedOut;
  private Boolean roundUpdates;
  private Integer buyInCollected;
  private Integer rebuyAddOnCollected;
  private Integer annualTocCollected;
  private Integer quarterlyTocCollected;
  private Integer chop;

  @Override
  public int compareTo(GamePlayer other) {

    // If I don't have a name
    if (name == null) {
      // then I come after other
      return 1;
    }

    // If other doesn't have a name
    if (other.name == null) {
      // then I come before other
      return -1;
    }

    return name.toLowerCase().compareTo(other.name.toLowerCase());
  }
}
