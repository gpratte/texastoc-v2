package com.texastoc.service;

import com.texastoc.model.config.TocConfig;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GamePayoutRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonPayoutRepository;
import com.texastoc.repository.SeasonPlayerRepository;
import com.texastoc.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    public SeasonService(SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameRepository gameRepository, ConfigRepository configRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonPlayerRepository seasonPlayerRepository, SeasonPayoutRepository seasonPayoutRepository) {
        this.seasonRepository = seasonRepository;
        this.qSeasonRepository = qSeasonRepository;
        this.gameRepository = gameRepository;
        this.configRepository = configRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gamePayoutRepository = gamePayoutRepository;
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.seasonPayoutRepository = seasonPayoutRepository;
    }

    @Transactional
    public Season createSeason(LocalDate start) {

        // TODO make sure this season starts after the last game of the previous season

        // TODO make sure not overlapping with another season
        // The end will be the day before the start date next year
        LocalDate end = start.plusYears(1).minusDays(1);

        TocConfig tocConfig = configRepository.get();

        // Count the number of Thursdays between the start and end
        int numThursdays = 0;
        LocalDate currentWeek = start;
        while (currentWeek.isBefore(end)) {
            ++numThursdays;
            currentWeek = currentWeek.plusDays(7);
        }

        List<QuarterlySeason> qSeasons = new ArrayList<>();
        for (int i = 1; i <= 4; ++i) {
            LocalDate qStart = start.plusWeeks(13 * (i - 1));

            LocalDate qEnd = start.plusWeeks(13 * i).minusDays(1);

            // Count the number of Thursdays between the start and end
            int qNumThursdays = 0;
            currentWeek = qStart;
            while (currentWeek.isBefore(qEnd)) {
                ++qNumThursdays;
                currentWeek = currentWeek.plusDays(7);
            }

            QuarterlySeason qSeason = QuarterlySeason.builder()
                .quarter(Quarter.fromInt(i))
                .start(qStart)
                .end(qEnd)
                .finalized(false)
                .numGames(qNumThursdays)
                .numGamesPlayed(0)
                .qTocCollected(0)
                .qTocPerGame(tocConfig.getQuarterlyTocCost())
                .numPayouts(tocConfig.getQuarterlyNumPayouts())
                .build();
            qSeasons.add(qSeason);
        }

        Season newSeason = Season.builder()
            .start(start)
            .end(end)
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
        Season season = seasonRepository.get(id);
        season.setQuarterlySeasons(qSeasonRepository.getBySeasonId(id));
        season.setGames(gameRepository.getBySeasonId(id));

        for (QuarterlySeason qSeason : season.getQuarterlySeasons()) {
            qSeason.setPlayers(seasonPlayerRepository.getByQuarterlySeasonId(qSeason.getId()));
            qSeason.setPayouts(seasonPayoutRepository.getByQuarterlySeasonId(qSeason.getId()));
        }

        for (Game game : season.getGames()) {
            game.setPlayers(gamePlayerRepository.selectByGameId(game.getId()));
            game.setPayouts(gamePayoutRepository.getByGameId(game.getId()));
        }
        return season;
    }

    @Transactional(readOnly = true)
    public Season getCurrentSeason() {
        Season season = seasonRepository.getCurrent();
        season.setQuarterlySeasons(qSeasonRepository.getBySeasonId(season.getId()));
        season.setGames(gameRepository.getBySeasonId(season.getId()));
        return season;
    }
}
