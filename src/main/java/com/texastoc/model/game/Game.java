package com.texastoc.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Game {

    private int id;
    private LocalDate date;
    private LocalTime start;
    private Integer hostId;
    private String hostName;
    private int seasonOrdinal;
    private int quarterlyOrdinal;
    private int numPlayers;
    private boolean doubleBuyIn;
    private int kitty;
    private int buyIn;
    private int rebuyAddOn;
    private int annualTocAmount;
    private int quarterlyTocAmount;
    private List<GamePlayer> players;
    private List<Payout> payouts;

    public void addPlayer(GamePlayer player) {
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
