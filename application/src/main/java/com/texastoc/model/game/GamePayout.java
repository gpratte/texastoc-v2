package com.texastoc.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GamePayout {

    private int gameId;
    private int place;
    private Integer amount;
    private Integer chopAmount;
    private Double chopPercent;
}
