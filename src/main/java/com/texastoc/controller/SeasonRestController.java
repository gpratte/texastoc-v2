package com.texastoc.controller;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SeasonRestController {

    private final SeasonService seasonService;

    @Autowired
    public SeasonRestController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @PostMapping("/api/v2/seasons")
    public Season createSeason(Season season) {
        return seasonService.createSeason(season);
    }

    @GetMapping("/api/v2/seasons/current")
    public Season getCurrentSeason() {

        Season season = Season.builder()
            .id(1)
            .start(LocalDate.now())
            .end(LocalDate.now())
            .build();

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

        SeasonPayout payout = new SeasonPayout();
        payout.setAmount(100);
        payout.setPlace(1);
        season.addPayout(payout);

        payout = new SeasonPayout();
        payout.setAmount(85);
        payout.setPlace(2);
        season.addPayout(payout);

        QuarterlySeason quarterlySeason = QuarterlySeason.builder()
            .id(1)
            .start(LocalDate.now())
            .end(LocalDate.now())
            .quarter(1)
            .tocCollected(445)
            .players(season.getPlayers())
            .payouts(season.getPayouts())
            .build();
        season.addQuarterlySeason(quarterlySeason);

        return season;
    }

}
