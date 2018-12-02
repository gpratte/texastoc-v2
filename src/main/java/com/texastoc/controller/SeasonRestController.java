package com.texastoc.controller;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
public class SeasonRestController {

    private final SeasonService seasonService;

    @Autowired
    public SeasonRestController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @PostMapping("/api/v2/seasons")
    public Season createSeason(@RequestBody @Valid Season season) {
        return seasonService.createSeason(season);
    }

    @GetMapping("/api/v2/seasons/{id}")
    public Season getCurrentSeason(@PathVariable("id") int id) {
        return seasonService.getSeason(id);
    }

}
