package com.texastoc.model.season;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
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
    private Boolean finalized;
    @NotNull(message = "quarter is required")
    @Min(1)
    private Quarter quarter;
    private Integer numGames;
    private Integer numGamesPlayed;
    private Integer tocCollected;
    @NotNull(message = "quarterly toc per game is required")
    @Min(0)
    private Integer tocPerGame;
    @NotNull(message = "number of payouts is required")
    @Min(1)
    private Integer numPayouts;
    private LocalDateTime lastCalculated;
    private List<SeasonPlayer> players;
    private List<SeasonPayout> payouts;

    public void addPlayer(SeasonPlayer player) {
        if (players == null) {
            players = new LinkedList<>();
        }
        players.add(player);
    }

    public void addPayout(SeasonPayout payout) {
        if (payouts == null) {
            payouts = new LinkedList<>();
        }
        payouts.add(payout);
    }

}
