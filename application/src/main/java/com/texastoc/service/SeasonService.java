package com.texastoc.service;

import com.texastoc.model.config.TocConfig;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.*;
import com.texastoc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SeasonService {

  private final SeasonRepository seasonRepository;
  private final QuarterlySeasonRepository qSeasonRepository;
  private final GameRepository gameRepository;
  private final GamePlayerRepository gamePlayerRepository;
  private final GamePayoutRepository gamePayoutRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final ConfigRepository configRepository;
  private final SeasonPayoutRepository seasonPayoutRepository;
  private final QuarterlySeasonPlayerRepository qSeasonPlayerRepository;
  private final QuarterlySeasonPayoutRepository qSeasonPayoutRepository;

  private Season cachedSeason = null;
  private ZoneId zoneId = ZoneId.of("US/Central");


  @Autowired
  public SeasonService(SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameRepository gameRepository, ConfigRepository configRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonPlayerRepository seasonPlayerRepository, SeasonPayoutRepository seasonPayoutRepository, QuarterlySeasonPlayerRepository qSeasonPlayerRepository, QuarterlySeasonPayoutRepository qSeasonPayoutRepository) {
    this.seasonRepository = seasonRepository;
    this.qSeasonRepository = qSeasonRepository;
    this.gameRepository = gameRepository;
    this.configRepository = configRepository;
    this.gamePlayerRepository = gamePlayerRepository;
    this.gamePayoutRepository = gamePayoutRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.seasonPayoutRepository = seasonPayoutRepository;
    this.qSeasonPlayerRepository = qSeasonPlayerRepository;
    this.qSeasonPayoutRepository = qSeasonPayoutRepository;
  }

  @Transactional
  public Season createSeason(int startYear) {

    ZonedDateTime start = ZonedDateTime.of(startYear, Month.MAY.getValue(), 1, 1, 1, 1, 1, zoneId);

    // TODO make sure not overlapping with another season
    // The end will be the day before the start date next year
    ZonedDateTime end = start.plusYears(1).minusDays(1);

    TocConfig tocConfig = configRepository.get();

    // Count the number of Thursdays between the start and end inclusive
    int numThursdays = 0;
    ZonedDateTime thursday = findNextThursday(start);
    while (thursday.isBefore(end) || thursday.isEqual(end)) {
      ++numThursdays;
      thursday = thursday.plusWeeks(1);
    }

    List<QuarterlySeason> qSeasons = createQuarterlySeasons(start, end, tocConfig);

    Season newSeason = Season.builder()
      .start(start.toLocalDate())
      .end(end.toLocalDate())
      .kittyPerGame(tocConfig.getKittyDebit())
      .tocPerGame(tocConfig.getAnnualTocCost())
      .quarterlyTocPerGame(tocConfig.getQuarterlyTocCost())
      .quarterlyNumPayouts(tocConfig.getQuarterlyNumPayouts())
      .buyInCost(tocConfig.getRegularBuyInCost())
      .rebuyAddOnCost(tocConfig.getRegularRebuyCost())
      .rebuyAddOnTocDebit(tocConfig.getRegularRebuyTocDebit())
      .doubleBuyInCost(tocConfig.getDoubleBuyInCost())
      .doubleRebuyAddOnCost(tocConfig.getDoubleRebuyCost())
      .doubleRebuyAddOnTocDebit(tocConfig.getDoubleRebuyTocDebit())
      .numGames(numThursdays)
      .quarterlySeasons(qSeasons)
      .build();

    int seasonId = seasonRepository.save(newSeason);
    newSeason.setId(seasonId);

    for (QuarterlySeason qSeason : qSeasons) {
      qSeason.setSeasonId(seasonId);
      int qSeasonId = qSeasonRepository.save(qSeason);
      qSeason.setId(qSeasonId);
    }

    return newSeason;
  }

  @Transactional(readOnly = true)
  public Season getSeason(int id) {

    // TODO move to a real in memory cache
//        if (cachedSeason != null) {
//            LocalDateTime lastCalculated = seasonRepository.getLastCalculated(id);
//
//            if (cachedSeason.getLastCalculated() == null) {
//                if (lastCalculated == null) {
//                    return cachedSeason;
//                }
//            } else if (cachedSeason.getLastCalculated().isEqual(lastCalculated)) {
//                return cachedSeason;
//            }
//        }

    Season season = seasonRepository.get(id);
    List<SeasonPlayer> seasonPlayers = seasonPlayerRepository.getBySeasonId(id);
    Collections.sort(seasonPlayers);
    season.setPlayers(seasonPlayers);
    season.setPayouts(seasonPayoutRepository.getBySeasonId(id));

    season.setQuarterlySeasons(qSeasonRepository.getBySeasonId(id));
    season.setGames(gameRepository.getBySeasonId(id));

    for (QuarterlySeason qSeason : season.getQuarterlySeasons()) {
      List<QuarterlySeasonPlayer> qSeasonPlayers = qSeasonPlayerRepository.getByQSeasonId(qSeason.getId());
      Collections.sort(qSeasonPlayers);
      qSeason.setPlayers(qSeasonPlayers);
      qSeason.setPayouts(qSeasonPayoutRepository.getByQSeasonId(qSeason.getId()));
    }

    for (Game game : season.getGames()) {
      game.setPlayers(gamePlayerRepository.selectByGameId(game.getId()));
      game.setPayouts(gamePayoutRepository.getByGameId(game.getId()));
    }

    cachedSeason = season;
    return season;
  }

  @Transactional(readOnly = true)
  public Season getCurrentSeason() {
    Season season = seasonRepository.getCurrent();
    season.setQuarterlySeasons(qSeasonRepository.getBySeasonId(season.getId()));
    season.setGames(gameRepository.getBySeasonId(season.getId()));
    return season;
  }

  @Transactional(readOnly = true)
  public int getCurrentSeasonId() {
    return seasonRepository.getCurrent().getId();
  }

  private List<QuarterlySeason> createQuarterlySeasons(ZonedDateTime seasonStart, ZonedDateTime seasonEnd, TocConfig tocConfig) {
    List<QuarterlySeason> qSeasons = new ArrayList<>(4);
    for (int i = 1; i <= 4; ++i) {
      ZonedDateTime qStart = null;
      ZonedDateTime qEnd = null;
      switch (i) {
        case 1:
          // Season start
          qStart = seasonStart;
          // Last day in July
          qEnd = ZonedDateTime.of(seasonStart.getYear(), Month.AUGUST.getValue(), 1, 1, 1, 1, 1, zoneId);
          qEnd = qEnd.minusDays(1);
          break;
        case 2:
          // First day in August
          qStart = ZonedDateTime.of(seasonStart.getYear(), Month.AUGUST.getValue(), 1, 1, 1, 1, 1, zoneId);
          // Last day in October
          qEnd = ZonedDateTime.of(seasonStart.getYear(), Month.NOVEMBER.getValue(), 1, 1, 1, 1, 1, zoneId);
          qEnd = qEnd.minusDays(1);
          break;
        case 3:
          // First day in November
          qStart = ZonedDateTime.of(seasonStart.getYear(), Month.NOVEMBER.getValue(), 1, 1, 1, 1, 1, zoneId);
          // Last day in January
          qEnd = ZonedDateTime.of(seasonStart.getYear() + 1, Month.FEBRUARY.getValue(), 1, 1, 1, 1, 1, zoneId);
          qEnd = qEnd.minusDays(1);
          break;
        case 4:
          // First day in February
          qStart = ZonedDateTime.of(seasonStart.getYear() + 1, Month.FEBRUARY.getValue(), 1, 1, 1, 1, 1, zoneId);
          // End of season
          qEnd = seasonEnd;
          break;
      }

      // Count the number of Thursdays between the start and end inclusive
      int qNumThursdays = 0;
      ZonedDateTime thursday = findNextThursday(qStart);
      while (thursday.isBefore(qEnd) || thursday.isEqual(qEnd)) {
        ++qNumThursdays;
        thursday = thursday.plusWeeks(1);
      }

      QuarterlySeason qSeason = QuarterlySeason.builder()
        .quarter(Quarter.fromInt(i))
        .start(qStart.toLocalDate())
        .end(qEnd.toLocalDate())
        .finalized(false)
        .numGames(qNumThursdays)
        .numGamesPlayed(0)
        .qTocCollected(0)
        .qTocPerGame(tocConfig.getQuarterlyTocCost())
        .numPayouts(tocConfig.getQuarterlyNumPayouts())
        .build();
      qSeasons.add(qSeason);
    }
    return qSeasons;
  }

  private ZonedDateTime findNextThursday(ZonedDateTime day) {
    while (true) {
      if (day.getDayOfWeek() == DayOfWeek.THURSDAY) {
        return day;
      }
      day = day.plusDays(1);
    }
  }
}
