package com.texastoc.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GamePlayer {

    private int id;
    private int playerId;
    private String name;
    private Integer place;
    private Integer points;
    private Integer buyIn;
    private Integer reBuy;
    private Integer annualToc;
    private Integer quarterlyToc;
    private Integer chop;

}
