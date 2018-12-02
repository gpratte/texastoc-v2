package com.texastoc.model.season;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuarterlySeason {

    private int id;
    private LocalDate start;
    private LocalDate end;
    private int numGames;
    private int quarter;
    private int quarterlyTocAmount;
    private List<SeasonPlayer> players;
    private List<Payout> payouts;

    public void addPlayer(SeasonPlayer player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
    }

    public void addPayout(Payout payout) {
        if (payouts == null) {
            payouts = new ArrayList<>();
        }
        payouts.add(payout);
    }

}
