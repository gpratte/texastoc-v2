package com.texastoc.model.season;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class Season {

    private int id;
    private LocalDate start;
    private LocalDate end;
    private int numGames;
    private int numGamesPlayed;
    private int buyInCollected;
    private int rebuyAddOnCollected;
    private int tocCollected;
    private int tocPerGame;
    private int kittyPerGame;
    private int quarterlyTocPerGame;
    private int quarterlyNumPayouts;
    private List<SeasonPlayer> players;
    private List<Payout> payouts;
    private List<QuarterlySeason> quarterlySeasons;

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

    public void addQuarterlySeason(QuarterlySeason quarterlySeason) {
        if (quarterlySeasons == null) {
            quarterlySeasons = new ArrayList<>();
        }
        quarterlySeasons.add(quarterlySeason);
    }

}
