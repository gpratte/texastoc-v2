package com.texastoc.job;

import com.texastoc.model.season.Season;
import com.texastoc.service.SeasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * When running with an embedded H2 database populate the current season with games.
 */
@Profile("!mysql")
@Slf4j
@Component
public class PopulationScheduler {

  private final SeasonService seasonService;

  public PopulationScheduler(SeasonService seasonService) {
    this.seasonService = seasonService;
  }

  @Scheduled(fixedDelay = 10000, initialDelay = 10000)
  public void populate() {
    createSeason();
  }

  private void createSeason() {
    LocalDate now = LocalDate.now();

    try {
      List<Season> seasons = seasonService.getSeasons();
      if (seasons.size() > 0) {
        return;
      }
      int year;
      switch (now.getMonth()) {
        case JANUARY:
        case FEBRUARY:
        case MARCH:
        case APRIL:
          // if before May then create for the previous year
          year = now.getYear() - 1;
          break;
        default:
          year = now.getYear();
      }
      Season season = seasonService.createSeason(year);
      log.info("created season " + season.getStart());
    } catch (Exception e) {
      // do nothing
    }
  }

  private void createGames() {
    LocalDate now = LocalDate.now();
  }

}
