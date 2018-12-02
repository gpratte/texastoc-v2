package com.texastoc.model.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private Integer id;
    private LocalDate date;
    private LocalDateTime start;
    private Integer hostId;
    private String hostName;
    private Integer seasonOrdinal;
    private Integer quarterlyOrdinal;
    private Integer numPlayers;
    private Boolean doubleBuyIn;
    private Integer kitty;
    private Integer buyIn;
    private Integer rebuyAddOn;
    private Integer annualTocAmount;
    private Integer quarterlyTocAmount;
    private List<GamePlayer> players;
    private List<GamePayout> payouts;

    public void addPlayer(GamePlayer player) {
        if (players == null) {
            players = new LinkedList<>();
        }
        players.add(player);
    }

    public void addPayout(GamePayout payout) {
        if (payouts == null) {
            payouts = new LinkedList<>();
        }
        payouts.add(payout);
    }
}
