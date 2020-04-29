package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlySeasonPlayer implements Comparable<QuarterlySeasonPlayer> {

  private int id;
  private int playerId;
  private int seasonId;
  private int qSeasonId;
  private String name;
  private int entries;
  private int points;
  private Integer place;

  @Override
  public int compareTo(QuarterlySeasonPlayer other) {
    // If I do not have a points and the other does then I come after
    if (points == 0) {
      if (other.points > 0) {
        return 1;
      }
    }

    // If I have points
    if (points > 0) {
      // the other does not then I come before other
      if (other.points == 0) {
        return -1;
      }
      // the other points are smaller than mine then I come before other
      if (points > other.points) {
        return -1;
      }
      // If the points are equal then we are the same
      if (points == other.points) {
        // This only matters if our points are not zero
        if (points > 0) {
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
