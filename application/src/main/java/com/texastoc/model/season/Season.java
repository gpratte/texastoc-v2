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

    private int id;
    @NotNull(message = "start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    // From TocConfig
    private int kittyPerGame;
    private int tocPerGame;
    private int quarterlyTocPerGame;
    private int quarterlyNumPayouts;
    private int buyInCost;
    private int rebuyAddOnCost;
    private int rebuyAddOnTocDebit;
    private int doubleBuyInCost;
    private int doubleRebuyAddOnCost;
    private int doubleRebuyAddOnTocDebit;

    // Runtime variables. End with "Collected" for physical money in
    // money in for game buy-in
    private int buyInCollected;
    // money in for rebuy add on
    private int rebuyAddOnCollected;
    // money in for annual toc
    private int annualTocCollected;
    // all physical money collected which is buy-in, rebuy add on, annual toc
    private int totalCollected;

    // Runtime variables. End with "Calculated" for the where the money goes
    // rebuy add on that goes to annual TOC
    private int annualTocFromRebuyAddOnCalculated;
    // rebuy add on minus amount that goes to annual toc
    private int rebuyAddOnLessAnnualTocCalculated;
    // annual toc, annual toc from rebuy add on
    private int totalCombinedAnnualTocCalculated;
    // amount that goes to the kitty for supplies
    private int kittyCalculated;
    // total collected minus total combined toc collected minus kitty
    private int prizePotCalculated;

    // Other runtime variables
    private int numGames;
    private int numGamesPlayed;
    private LocalDateTime lastCalculated;
    private boolean finalized;

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
