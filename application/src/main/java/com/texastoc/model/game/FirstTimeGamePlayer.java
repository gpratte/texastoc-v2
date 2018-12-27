package com.texastoc.model.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirstTimeGamePlayer {
    private String firstName;
    private String lastName;
    private String email;
    private int gameId;
    private Integer buyInCollected;
    private Integer annualTocCollected;
    private Integer quarterlyTocCollected;
}
