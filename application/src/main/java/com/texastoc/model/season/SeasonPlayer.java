package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonPlayer {

    private int playerId;
    private int seasonId;
    private String name;
    private int entries;
    private int points;
    private Integer place;
    private boolean forfeit;

}
