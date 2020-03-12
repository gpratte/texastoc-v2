package com.texastoc.controller;

import com.texastoc.model.season.Season;
import com.texastoc.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@SuppressWarnings("unused")
@RestController
public class SeasonRestController {

  private final SeasonService seasonService;

  @Autowired
  public SeasonRestController(SeasonService seasonService) {
    this.seasonService = seasonService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/api/v2/seasons")
  public Season createSeason(@RequestBody LocalDate start) {
    return seasonService.createSeason(start);
  }

  @GetMapping("/api/v2/seasons/{id}")
  public Season getSeason(@PathVariable("id") int id) {
    return seasonService.getSeason(id);
  }

  @GetMapping("/api/v2/seasons/current")
  public Season getCurrentSeason() {
    return seasonService.getCurrentSeason();
  }

}
