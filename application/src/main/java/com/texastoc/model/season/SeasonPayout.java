package com.texastoc.model.season;

import lombok.Data;

@Data
public class SeasonPayout {

  private int id;
  private int seasonId;
  private int place;
  private int amount;
}
