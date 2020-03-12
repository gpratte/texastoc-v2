package com.texastoc.model.game.clock;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Round {
  private int round;
  private int smallBlind;
  private int bigBlind;
  private int ante;
}
