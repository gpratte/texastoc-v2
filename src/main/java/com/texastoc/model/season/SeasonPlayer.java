package com.texastoc.model.season;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeasonPlayer {

    private int playerId;
    private String name;
    private Integer place;
    private Integer points;
    private Integer entries;

}
