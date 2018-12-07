package com.texastoc.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final QuarterlySeasonRepository qSeasonRepository;
    private final GameRepository gameRepository;

    @Autowired
    public SeasonService(SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameRepository gameRepository) {
        this.seasonRepository = seasonRepository;
        this.qSeasonRepository = qSeasonRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Season createSeason(Season season) {

        // TODO make sure this season starts after the last game of the previous season
        LocalDate start = season.getStart();

        // TODO make sure not overlapping with another season
        // The end will be the day before the start date next year
        LocalDate end = season.getStart().plusYears(1).minusDays(1);

        // Count the number of Thursdays between the start and end
        int numThursdays = 0;
        LocalDate currentWeek = start;
        while (currentWeek.isBefore(end)) {
            ++numThursdays;
            currentWeek = currentWeek.plusDays(7);
        }

        List<QuarterlySeason> qSeasons = new ArrayList<>();
        for (int i = 1; i <= 4; ++i) {
            LocalDate qStart = season.getStart().plusWeeks(13 * (i - 1));

            LocalDate qEnd = season.getStart().plusWeeks(13 * i).minusDays(1);

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
                .tocCollected(0)
                .tocPerGame(season.getQuarterlyTocPerGame())
                .numPayouts(season.getQuarterlyNumPayouts())
                .build();
            qSeasons.add(qSeason);

        }

        Season newSeason = Season.builder()
            .start(start)
            .end(end)
            .finalized(false)
            .numGames(numThursdays)
            .numGamesPlayed(0)
            .buyInCollected(0)
            .rebuyAddOnCollected(0)
            .tocCollected(0)
            .tocPerGame(season.getTocPerGame())
            .kittyPerGame(season.getKittyPerGame())
            .quarterlyTocPerGame(season.getQuarterlyTocPerGame())
            .quarterlyNumPayouts(season.getQuarterlyNumPayouts())
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
        return season;
    }
}
