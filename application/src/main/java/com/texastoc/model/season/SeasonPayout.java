package com.texastoc.model.season;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeasonPayout {

    private int id;
    private int seasonId;
    private int quarterlySeasonId;
    private int place;
    private int amount;
}
