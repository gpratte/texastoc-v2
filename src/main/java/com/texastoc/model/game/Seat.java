package com.texastoc.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Seat {

    private int number;
    private int gamePlayerId;
    private String gamePlayerName;
}
