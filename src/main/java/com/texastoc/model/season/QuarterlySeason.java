package com.texastoc.model.season;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlySeason {

    private Integer id;
    private Integer seasonId;
    @NotNull(message = "start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
    @NotNull(message = "toc per game is required")
    @Min(1)
    private Integer quarter;
    private Integer numGames;
    private Integer numGamesPlayed;
    private Integer tocCollected;
    @NotNull(message = "quarterly toc per game is required")
    @Min(0)
    private Integer tocPerGame;
    @NotNull(message = "number of payouts is required")
    @Min(1)
    private Integer numPayouts;
    private List<SeasonPlayer> players;
    private List<SeasonPayout> payouts;

    public void addPlayer(SeasonPlayer player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
    }

    public void addPayout(SeasonPayout payout) {
        if (payouts == null) {
            payouts = new ArrayList<>();
        }
        payouts.add(payout);
    }

}
