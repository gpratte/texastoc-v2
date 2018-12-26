package com.texastoc.service;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.GamePayoutRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import com.texastoc.service.calculator.GameCalculator;
import com.texastoc.service.calculator.PayoutCalculator;
import com.texastoc.service.calculator.PointsCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GamePayoutRepository gamePayoutRepository;
    private final SeasonRepository seasonRepository;
    private final QuarterlySeasonRepository qSeasonRepository;
    private final GameCalculator gameCalculator;
    private final PayoutCalculator payoutCalculator;
    private final PointsCalculator pointsCalculator;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameCalculator gameCalculator, PayoutCalculator payoutCalculator, PointsCalculator pointsCalculator) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gamePayoutRepository = gamePayoutRepository;
        this.seasonRepository = seasonRepository;
        this.qSeasonRepository = qSeasonRepository;
        this.gameCalculator = gameCalculator;
        this.payoutCalculator = payoutCalculator;
        this.pointsCalculator = pointsCalculator;
    }

    @Transactional
    public Game createGame(Game game) {
        Game gameToCreate = new Game();

        // TODO check that date is allowed - not before an existing game and not beyond the season.
        gameToCreate.setDate(game.getDate());

        // TODO exception if null
        QuarterlySeason currentQSeason = qSeasonRepository.getCurrent();
        gameToCreate.setSeasonId(currentQSeason.getSeasonId());
        gameToCreate.setQSeasonId(currentQSeason.getId());
        gameToCreate.setQuarter(currentQSeason.getQuarter());

        Player player = playerRepository.get(game.getHostId());
        gameToCreate.setHostId(game.getHostId());
        gameToCreate.setHostName(player.getName());

        // Game setup variables
        gameToCreate.setDoubleBuyIn(game.getDoubleBuyIn());
        gameToCreate.setTransportRequired(game.getTransportRequired());

        Season currentSeason = seasonRepository.getCurrent();
        gameToCreate.setKittyCost(currentSeason.getKittyPerGame());
        gameToCreate.setBuyInCost(currentSeason.getBuyInCost());
        gameToCreate.setRebuyAddOnCost(currentSeason.getRebuyAddOnCost());
        gameToCreate.setRebuyAddOnTocDebit(currentSeason.getRebuyAddOnTocDebit());
        gameToCreate.setAnnualTocCost(currentSeason.getTocPerGame());
        gameToCreate.setQuarterlyTocCost(currentSeason.getQuarterlyTocPerGame());

        // Game time variables
        gameToCreate.setNumPlayers(0);
        gameToCreate.setKittyCollected(0);
        gameToCreate.setBuyInCollected(0);
        gameToCreate.setRebuyAddOnCollected(0);
        gameToCreate.setAnnualTocCollected(0);
        gameToCreate.setQuarterlyTocCollected(0);

        if (game.getDoubleBuyIn()) {
            gameToCreate.setBuyInCost(currentSeason.getDoubleBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getDoubleRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getDoubleRebuyAddOnTocDebit());
        } else {
            gameToCreate.setBuyInCost(currentSeason.getBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getRebuyAddOnTocDebit());
        }

        gameToCreate.setFinalized(false);

        int id = gameRepository.save(gameToCreate);
        gameToCreate.setId(id);

        return gameToCreate;
    }

    @Transactional(readOnly = true)
    public Game getGame(int id) {
        Game game = gameRepository.getById(id);
        game.setPlayers(gamePlayerRepository.selectByGameId(id));
        game.setPayouts(gamePayoutRepository.getByGameId(id));
        return game;
    }

    @Transactional
    public GamePlayer createGamePlayer(GamePlayer gamePlayer) {
        int gamePlayerId = gamePlayerRepository.save(gamePlayer);
        gamePlayer.setId(gamePlayerId);

        int gameId = gamePlayer.getGameId();
        Game currentGame = gameRepository.getById(gameId);
        List<GamePlayer> gamePlayers = gamePlayerRepository.selectByGameId(gameId);

        Game calculatedGame = gameCalculator.calculate(currentGame, gamePlayers);
        payoutCalculator.calculate(calculatedGame, gamePlayers);
        pointsCalculator.calculate(calculatedGame, gamePlayers);

        return gamePlayer;
    }
}
