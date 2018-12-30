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
public class SeasonPayout {

    private int id;
    private int seasonId;
    private int qSeasonId;
    private int place;
    private int amount;
}
