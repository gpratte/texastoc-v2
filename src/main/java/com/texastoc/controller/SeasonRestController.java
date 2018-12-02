package com.texastoc.controller;

import com.texastoc.model.season.Payout;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPlayer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SeasonRestController {

    @GetMapping("/api/v2/seasons/current")
    public Season getCurrentSeason() {

        Season season = new Season();
        season.setId(1);
        season.setStart(LocalDate.now());
        season.setEnd(LocalDate.now());

        SeasonPlayer player = new SeasonPlayer();
        player.setPlayerId(100);
        player.setName("Tom");
        player.setPlace(1);
        player.setPoints(100);
        player.setEntries(2);
        season.addPlayer(player);

        player = new SeasonPlayer();
        player.setPlayerId(101);
        player.setName("Dick");
        player.setPlace(2);
        player.setPoints(88);
        player.setEntries(1);
        season.addPlayer(player);

        player = new SeasonPlayer();
        player.setPlayerId(102);
        player.setName("Harry");
        season.addPlayer(player);

        Payout payout = new Payout();
        payout.setAmount(100);
        payout.setPlace(1);
        season.addPayout(payout);

        payout = new Payout();
        payout.setAmount(85);
        payout.setPlace(2);
        season.addPayout(payout);

        QuarterlySeason quarterlySeason = new QuarterlySeason();
        season.addQuarterlySeason(quarterlySeason);
        quarterlySeason.setId(1);
        quarterlySeason.setStart(LocalDate.now());
        quarterlySeason.setEnd(LocalDate.now());
        quarterlySeason.setQuarter(1);
        quarterlySeason.setQuarterlyTocAmount(445);
        quarterlySeason.setPlayers(season.getPlayers());
        quarterlySeason.setPayouts(season.getPayouts());

        return season;
    }

}
