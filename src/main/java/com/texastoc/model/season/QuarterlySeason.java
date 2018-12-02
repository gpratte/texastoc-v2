package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class QuarterlySeason {

    private int id;
    private int seasonId;
    private LocalDate start;
    private LocalDate end;
    private int quarter;
    private int numGames;
    private int numGamesPlayed;
    private int tocCollected;
    private int tocPerGame;
    private int numPayouts;
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
