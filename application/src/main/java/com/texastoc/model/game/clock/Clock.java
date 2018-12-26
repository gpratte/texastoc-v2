package com.texastoc.model.game.clock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Clock {

    private int clockId;
    private int minutes;
    private int seconds;
    private boolean playing;
    private Round thisRound;
    private Round nextRound;
}
