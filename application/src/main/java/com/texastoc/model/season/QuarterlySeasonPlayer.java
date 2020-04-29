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
    // If I do not have a place and the other does
    if (place == null) {
      if (other.place != null) {
        return 1;
      }
    }

    // If I have a place and the other either does not or is higher than mine then I come before other
    if (place != null) {
      if (other.place == null) {
        return -1;
      }
      if (place.intValue() < other.place.intValue()) {
        return -1;
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
