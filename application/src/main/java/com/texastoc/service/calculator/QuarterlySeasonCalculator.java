package com.texastoc.service.calculator;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.QuarterlySeasonPayout;
import com.texastoc.model.season.QuarterlySeasonPlayer;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonPayoutRepository;
import com.texastoc.repository.QuarterlySeasonPlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonPlayerRepository;
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
    private final QuarterlySeasonPlayerRepository qSeasonPlayerRepository;
    private final QuarterlySeasonPayoutRepository qSeasonPayoutRepository;

    public QuarterlySeasonCalculator(QuarterlySeasonRepository qSeasonRepository, GamePlayerRepository gamePlayerRepository, GameRepository gameRepository, QuarterlySeasonPlayerRepository qSeasonPlayerRepository, QuarterlySeasonPayoutRepository qSeasonPayoutRepository) {
        this.qSeasonRepository = qSeasonRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameRepository = gameRepository;
        this.qSeasonPlayerRepository = qSeasonPlayerRepository;
        this.qSeasonPayoutRepository = qSeasonPayoutRepository;
    }

    public QuarterlySeason calculate(int id) {
        QuarterlySeason qSeason = qSeasonRepository.getById(id);

        // Calculate quarterly season
        List<Game> games = gameRepository.getByQuarterlySeasonId(id);

        qSeason.setNumGamesPlayed(games.size());

        int qTocCollected = 0;
        for (Game game : games) {
            qTocCollected += game.getQuarterlyTocCollected();
        }
        qSeason.setQTocCollected(qTocCollected);

        // Persist quarterly season
        qSeasonRepository.update(qSeason);


        // Calculate quarterly season players
        List<QuarterlySeasonPlayer> players = calculatePlayers(qSeason.getSeasonId(), id);
        qSeason.setPlayers(players);

        // Persist quarterly season players
        qSeasonPlayerRepository.deleteByQSeasonId(id);
        for (QuarterlySeasonPlayer player : players) {
            qSeasonPlayerRepository.save(player);
        }

        // Calculate quarterly season payouts
        List<QuarterlySeasonPayout> payouts = calculatePayouts(qTocCollected, qSeason.getSeasonId(), id);
        qSeason.setPayouts(payouts);

        // Persist quarterly season payouts
        qSeasonPayoutRepository.deleteByQSeasonId(id);
        for (QuarterlySeasonPayout payout : payouts) {
            qSeasonPayoutRepository.save(payout);
        }

        return qSeason;
    }

    private List<QuarterlySeasonPlayer> calculatePlayers(int seasonId, int qSeasonId) {

        Map<Integer, QuarterlySeasonPlayer> seasonPlayerMap = new HashMap<>();

        List<GamePlayer> gamePlayers = gamePlayerRepository.selectQuarterlyTocPlayersByQuarterlySeasonId(qSeasonId);

        for (GamePlayer gamePlayer : gamePlayers) {
            QuarterlySeasonPlayer player = seasonPlayerMap.get(gamePlayer.getId());
            if (player == null) {

                player = QuarterlySeasonPlayer.builder()
                    .playerId(gamePlayer.getPlayerId())
                    .seasonId(seasonId)
                    .qSeasonId(qSeasonId)
                    .name(gamePlayer.getName())
                    .build();

                seasonPlayerMap.put(gamePlayer.getId(), player);
            }

            if (gamePlayer.getPoints() != null && gamePlayer.getPoints() > 0) {
                player.setPoints(player.getPoints() + gamePlayer.getPoints());
            }

            player.setEntries(player.getEntries() + 1);
        }

        List<QuarterlySeasonPlayer> players = new ArrayList<>(seasonPlayerMap.values());

        // Sort and set the place
        players.sort( (p1,p2) -> p2.getPoints() - p1.getPoints());
        int place = 1;
        for (QuarterlySeasonPlayer player : players) {
            if (player.getPoints() > 0) {
                player.setPlace(place);
                ++place;
            }
        }

        return players;
    }

    private List<QuarterlySeasonPayout> calculatePayouts(int pot, int seasonId, int qSeasonId) {
        List<QuarterlySeasonPayout> payouts = new ArrayList<>(3);

        if (pot < 1) {
            return payouts;
        }

        int firstPlace = (int) Math.round(pot * 0.5d);
        int secondPlace = (int) Math.round(pot * 0.3d);
        int thirdPlace = pot - firstPlace - secondPlace;

        payouts.add(QuarterlySeasonPayout.builder()
            .seasonId(seasonId)
            .qSeasonId(qSeasonId)
            .place(1)
            .amount(firstPlace)
            .build());
        payouts.add(QuarterlySeasonPayout.builder()
            .seasonId(seasonId)
            .qSeasonId(qSeasonId)
            .place(2)
            .amount(secondPlace)
            .build());
        payouts.add(QuarterlySeasonPayout.builder()
            .seasonId(seasonId)
            .qSeasonId(qSeasonId)
            .place(3)
            .amount(thirdPlace)
            .build());

        return payouts;
    }
}