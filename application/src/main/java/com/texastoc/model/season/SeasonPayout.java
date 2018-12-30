package com.texastoc.model.season;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
public class SeasonPayout {

    private int id;
    private int seasonId;
    private int quarterlySeasonId;
    private int place;
    private int amount;
}
