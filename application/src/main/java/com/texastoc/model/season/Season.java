package com.texastoc.model.season;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.texastoc.model.game.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Season {

    private Integer id;
    @NotNull(message = "start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    // From TocConfig
    private Integer kittyPerGame;
    private Integer tocPerGame;
    private Integer quarterlyTocPerGame;
    private Integer quarterlyNumPayouts;
    private Integer buyInCost;
    private Integer rebuyAddOnCost;
    private Integer rebuyAddOnTocDebit;
    private Integer doubleBuyInCost;
    private Integer doubleRebuyAddOnCost;
    private Integer doubleRebuyAddOnTocDebit;

    // Runtime
    private Integer numGames;
    private Integer numGamesPlayed;
    private Integer buyInCollected;
    private Integer rebuyAddOnCollected;
    private Integer tocCollected;
    private LocalDateTime lastCalculated;
    private Boolean finalized;

    private List<SeasonPlayer> players;
    private List<SeasonPayout> payouts;
    private List<QuarterlySeason> quarterlySeasons;
    private List<Game> games;


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

    public void addQuarterlySeason(QuarterlySeason quarterlySeason) {
        if (quarterlySeasons == null) {
            quarterlySeasons = new ArrayList<>(4);
        }
        quarterlySeasons.add(quarterlySeason);
    }

    public void addGame(Game game) {
        if (games == null) {
            games = new LinkedList<>();
        }
        games.add(game);
    }

}
