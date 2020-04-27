package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeasonPlayer implements Comparable<SeasonPlayer> {

  private int id;
  private int playerId;
  private int seasonId;
  private String name;
  private int entries;
  private int points;
  private Integer place;
  private boolean forfeit;

  @Override
  public int compareTo(SeasonPlayer o) {
    if (this.points < o.points) {
      return 1;
    } else if (this.points > o.points) {
      return -1;
    } else {
      return 0;
    }
  }
}
