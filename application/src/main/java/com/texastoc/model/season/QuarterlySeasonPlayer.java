package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlySeasonPlayer {

    private int id;
    private int playerId;
    private int seasonId;
    private int qSeasonId;
    private String name;
    private int entries;
    private int points;
    private Integer place;

}
