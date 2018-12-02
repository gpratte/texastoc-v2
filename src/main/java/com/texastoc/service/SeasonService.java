package com.texastoc.service;

import com.texastoc.model.season.Payout;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPlayer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeasonService {

    public Season createSeason(Season season) {

        // TODO make sure not overlapping with another season
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
        for (int i = 1; i <=4; ++i) {
            LocalDate qStart = season.getStart().plusWeeks(13 * (i-1));

            LocalDate qEnd = season.getStart().plusWeeks(13 * i).minusDays(1);

            // Count the number of Thursdays between the start and end
            int qNumThursdays = 0;
            currentWeek = qStart;
            while (currentWeek.isBefore(qEnd)) {
                ++qNumThursdays;
                currentWeek = currentWeek.plusDays(7);
            }

            QuarterlySeason qSeason = QuarterlySeason.builder()
                .id(1)
                .quarter(i)
                .start(qStart)
                .end(qEnd)
                .numGames(qNumThursdays)
                .tocPerGame(season.getQuarterlyTocPerGame())
                .numPayouts(season.getQuarterlyNumPayouts())
                .build();
            qSeasons.add(qSeason);

        }

        return Season.builder()
            .id(1)
            .start(start)
            .end(end)
            .kittyPerGame(season.getKittyPerGame())
            .tocPerGame(season.getTocPerGame())
            .quarterlyTocPerGame(season.getQuarterlyTocPerGame())
            .quarterlyNumPayouts(season.getQuarterlyNumPayouts())
            .numGames(numThursdays)
            .quarterlySeasons(qSeasons)
            .build();

    }
}
