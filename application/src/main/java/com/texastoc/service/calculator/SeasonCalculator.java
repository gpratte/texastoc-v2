package com.texastoc.service.calculator;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.SeasonPayoutRepository;
import com.texastoc.repository.SeasonPlayerRepository;
import com.texastoc.repository.SeasonRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SeasonCalculator {

    private final GameRepository gameRepository;
    private final SeasonRepository seasonRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final SeasonPlayerRepository seasonPlayerRepository;
    private final SeasonPayoutRepository seasonPayoutRepository;

    public SeasonCalculator(GameRepository gameRepository, SeasonRepository seasonRepository, SeasonPlayerRepository seasonPlayerRepository, GamePlayerRepository gamePlayerRepository, SeasonPayoutRepository seasonPayoutRepository) {
        this.gameRepository = gameRepository;
        this.seasonRepository = seasonRepository;
        this.seasonPlayerRepository = seasonPlayerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.seasonPayoutRepository = seasonPayoutRepository;
    }

    @SuppressWarnings("Duplicates")
    public Season calculate(int id) {

        Season season = seasonRepository.get(id);

        // Calculate season
        List<Game> games = gameRepository.getBySeasonId(id);

        season.setNumGamesPlayed(games.size());

        int buyInCollected = 0;
        int rebuyAddOnCollected = 0;
        int annualTocCollected = 0;
        int totalCollected = 0;

        int annualTocFromRebuyAddOnCalculated = 0;
        int rebuyAddOnLessAnnualTocCalculated = 0;
        int totalCombinedAnnualTocCalculated = 0;
        int kittyCalculated = 0;
        int prizePotCalculated = 0;

        for (Game game : games) {
            buyInCollected += game.getBuyInCollected();
            rebuyAddOnCollected += game.getRebuyAddOnCollected();
            annualTocCollected += game.getAnnualTocCollected();
            totalCollected += game.getTotalCollected();

            annualTocFromRebuyAddOnCalculated += game.getAnnualTocFromRebuyAddOnCalculated();
            rebuyAddOnLessAnnualTocCalculated += game.getRebuyAddOnLessAnnualTocCalculated();
            totalCombinedAnnualTocCalculated += game.getTotalCombinedTocCalculated();
            kittyCalculated += game.getKittyCalculated();
            prizePotCalculated += game.getPrizePotCalculated();
        }

        season.setBuyInCollected(buyInCollected);
        season.setRebuyAddOnCollected(rebuyAddOnCollected);
        season.setAnnualTocCollected(annualTocCollected);
        season.setTotalCollected(totalCollected);

        season.setAnnualTocFromRebuyAddOnCalculated(annualTocFromRebuyAddOnCalculated);
        season.setRebuyAddOnLessAnnualTocCalculated(rebuyAddOnLessAnnualTocCalculated);
        season.setTotalCombinedAnnualTocCalculated(totalCombinedAnnualTocCalculated);
        season.setKittyCalculated(kittyCalculated);
        season.setPrizePotCalculated(prizePotCalculated);

        season.setLastCalculated(LocalDateTime.now());

        // Persist season
        seasonRepository.update(season);

        // Calculate season players
        List<SeasonPlayer> players = calculatePlayers(id);
        season.setPlayers(players);

        // Persist season players
        seasonPlayerRepository.deleteBySeasonId(id);
        for (SeasonPlayer player : players) {
            seasonPlayerRepository.save(player);
        }

        // Calculate season payouts
        // TODO need to test once I know what the season payouts are
        List<SeasonPayout> payouts = new ArrayList<>(10);
        season.setPayouts(payouts);

        // Persist season payouts
        seasonPayoutRepository.deleteBySeasonId(id);
        for (SeasonPayout payout : payouts) {
            seasonPayoutRepository.save(payout);
        }

        return season;
    }

    @SuppressWarnings("Duplicates")
    private List<SeasonPlayer> calculatePlayers(int id) {

        Map<Integer, SeasonPlayer> seasonPlayerMap = new HashMap<>();

        List<GamePlayer> gamePlayers = gamePlayerRepository.selectAnnualTocPlayersBySeasonId(id);
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

}
