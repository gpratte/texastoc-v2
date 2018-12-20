package com.texastoc.model.season;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeasonPlayer {

    private int id;
    private int playerId;
    private int seasonId;
    private int quarterlySeasonId;
    private String name;
    private Integer place;
    private Integer points;
    private Integer entries;

}
