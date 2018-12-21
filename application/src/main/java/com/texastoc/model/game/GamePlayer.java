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
    private int gameId;
    private String name;
    private Integer points;
    private Integer finish;
    private Boolean knockedOut;
    private Boolean roundUpdates;
    private Integer buyInCollected;
    private Integer rebuyAddOnCollected;
    private Integer annualTocCollected;
    private Integer quarterlyTocCollected;
    private Integer chop;

}
