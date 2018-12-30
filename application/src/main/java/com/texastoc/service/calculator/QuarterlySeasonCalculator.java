package com.texastoc.service.calculator;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QuarterlySeasonCalculator {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final QuarterlySeasonRepository qSeasonRepository;

    public QuarterlySeasonCalculator(QuarterlySeasonRepository qSeasonRepository, GamePlayerRepository gamePlayerRepository, GameRepository gameRepository) {
        this.qSeasonRepository = qSeasonRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameRepository = gameRepository;
    }

    public QuarterlySeason calculate(int id) {
        QuarterlySeason qSeason = qSeasonRepository.getById(id);

        List<Game> games = gameRepository.getByQuarterlySeasonId(id);

        qSeason.setNumGamesPlayed(games.size());

        int qTocCollected = 0;
        for (Game game : games) {
            qTocCollected += game.getQuarterlyTocCollected();
        }
        qSeason.setQTocCollected(qTocCollected);

        List<SeasonPlayer> seasonPlayers = calculatePlayers(id);
        qSeason.setPlayers(seasonPlayers);

        List<SeasonPayout> payouts = calculatePayouts(qTocCollected, qSeason.getSeasonId(), qSeason.getId());
        qSeason.setPayouts(payouts);

        return qSeason;
    }

    private List<SeasonPlayer> calculatePlayers(int id) {

        Map<Integer, SeasonPlayer> seasonPlayerMap = new HashMap<>();

        List<GamePlayer> gamePlayers = gamePlayerRepository.selectQuarterlyTocPlayersByQuarterlySeasonId(id);

        for (GamePlayer gamePlayer : gamePlayers) {
            SeasonPlayer seasonPlayer = seasonPlayerMap.get(gamePlayer.getId());
            if (seasonPlayer == null) {

                seasonPlayer = SeasonPlayer.builder()
                    .playerId(gamePlayer.getPlayerId())
                    .seasonId(id)
                    .name(gamePlayer.getName())
                    .build();

                seasonPlayerMap.put(gamePlayer.getId(), seasonPlayer);
            }

            if (gamePlayer.getPoints() != null && gamePlayer.getPoints() > 0) {
                seasonPlayer.setPoints(seasonPlayer.getPoints() + gamePlayer.getPoints());
            }

            seasonPlayer.setEntries(seasonPlayer.getEntries() + 1);
        }

        return new ArrayList<>(seasonPlayerMap.values());
    }

    private List<SeasonPayout> calculatePayouts(int pot, int seasonId, int qSeasonId) {
        List<SeasonPayout> payouts = new ArrayList<>(3);

        if (pot < 1) {
            return payouts;
        }

        int firstPlace = (int) Math.round(pot * 0.5d);
        int secondPlace = (int) Math.round(pot * 0.3d);
        int thirdPlace = pot - firstPlace - secondPlace;

        payouts.add(SeasonPayout.builder()
            .seasonId(seasonId)
            .quarterlySeasonId(qSeasonId)
            .place(1)
            .amount(firstPlace)
            .build());
        payouts.add(SeasonPayout.builder()
            .seasonId(seasonId)
            .quarterlySeasonId(qSeasonId)
            .place(2)
            .amount(secondPlace)
            .build());
        payouts.add(SeasonPayout.builder()
            .seasonId(seasonId)
            .quarterlySeasonId(qSeasonId)
            .place(3)
            .amount(thirdPlace)
            .build());

        return payouts;
    }
}