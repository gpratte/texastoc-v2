package com.texastoc.model.season;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuarterlySeasonPlayer {

    private int playerId;
    private int seasonId;
    private int qSeasonId;
    private String name;
    private int entries;
    private int points;
    private Integer place;

}
