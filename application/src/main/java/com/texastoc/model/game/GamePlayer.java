package com.texastoc.model.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayer {

    private int id;
    private int playerId;
    private int qSeasonId;
    private int seasonId;
    private int gameId;
    private String name;
    // TODO private String firstName;
    // TODO private String lastName;
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
