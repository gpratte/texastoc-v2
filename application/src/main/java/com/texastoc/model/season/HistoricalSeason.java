package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalSeason {
  private String startYear;
  private String endYear;
  private List<HistoricalSeasonPlayer> players;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class HistoricalSeasonPlayer {
    private String firstName;
    private String lastName;
    private int points;
    private int entries;
  }
}
