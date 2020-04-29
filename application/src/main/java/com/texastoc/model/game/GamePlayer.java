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
    // If I do not have a points and the other does then I come after
    if (points == null) {
      if (other.points != null) {
        return 1;
      }
    }

    // If I have points
    if (points != null) {
      // the other does not then I come before other
      if (other.points == null) {
        return -1;
      }
      // the other points are smaller than mine then I come before other
      if (points.intValue() > other.points.intValue()) {
        return -1;
      }
      // If the points are equal then we are the same
      if (points.intValue() == other.points.intValue()) {
        // This only matters if our points are not zero
        if (points.intValue() > 0) {
          return 0;
        }
      }
    }

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
