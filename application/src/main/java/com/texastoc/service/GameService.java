package com.texastoc.service;

import com.texastoc.exception.DoubleBuyInMismatchException;
import com.texastoc.model.config.TocConfig;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.ConfigRepository;
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
import java.util.Objects;

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
    private final ConfigRepository configRepository;
    private TocConfig tocConfig;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameCalculator gameCalculator, PayoutCalculator payoutCalculator, PointsCalculator pointsCalculator, ConfigRepository configRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gamePayoutRepository = gamePayoutRepository;
        this.seasonRepository = seasonRepository;
        this.qSeasonRepository = qSeasonRepository;
        this.gameCalculator = gameCalculator;
        this.payoutCalculator = payoutCalculator;
        this.pointsCalculator = pointsCalculator;
        this.configRepository = configRepository;
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
    public void updateGame(Game game) {
        gameRepository.update(game);
    }

    @Transactional
    public GamePlayer createGamePlayer(GamePlayer gamePlayer) {

        return this.createGamePlayerWorker(gamePlayer);
    }

    // Worker to avoid one @Transacation calling anther @Transactional
    private GamePlayer createGamePlayerWorker(GamePlayer gamePlayer) {

        int gameId = gamePlayer.getGameId();
        Game currentGame = gameRepository.getById(gameId);

        // Make sure money is right
        verifyGamePlayerMoney(currentGame.getDoubleBuyIn(), gamePlayer);

        if (gamePlayer.getName() == null) {
            Player player = playerRepository.get(gamePlayer.getPlayerId());
            gamePlayer.setName(player.getName());
        }

        int gamePlayerId = gamePlayerRepository.save(gamePlayer);
        gamePlayer.setId(gamePlayerId);

        recalculate(currentGame);

        return gamePlayer;
    }
    @Transactional
    public void updateGamePlayer(GamePlayer gamePlayer) {
        int gameId = gamePlayer.getGameId();
        Game currentGame = gameRepository.getById(gameId);

        // Make sure money is right
        verifyGamePlayerMoney(currentGame.getDoubleBuyIn(), gamePlayer);

        gamePlayerRepository.update(gamePlayer);

        recalculate(currentGame);
    }

    @Transactional
    public void deleteGamePlayer(int gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepository.selectById(gamePlayerId);
        gamePlayerRepository.deleteById(gamePlayer.getId());

        Game currentGame = gameRepository.getById(gamePlayer.getGameId());
        recalculate(currentGame);
    }

    @Transactional(readOnly = true)
    public GamePlayer getGamePlayer(int gamePlayerId) {
        return gamePlayerRepository.selectById(gamePlayerId);
    }

    private void verifyGamePlayerMoney(boolean doubleBuyIn, GamePlayer gamePlayer) {
        TocConfig tocConfig = getTocConfig();
        Integer buyIn = gamePlayer.getBuyInCollected();
        Integer rebuyAddOn = gamePlayer.getRebuyAddOnCollected();
        Integer toc = gamePlayer.getAnnualTocCollected();
        Integer qToc = gamePlayer.getQuarterlyTocCollected();

        if (doubleBuyIn) {
            if (buyIn != null && buyIn != tocConfig.getDoubleBuyInCost()) {
                throw new DoubleBuyInMismatchException("Buy-in should be double");
            }
            if (rebuyAddOn != null && rebuyAddOn != tocConfig.getDoubleRebuyCost()) {
                throw new DoubleBuyInMismatchException("Rebuy/AddOn should be double");
            }
        } else {
            if (buyIn != null && buyIn == tocConfig.getDoubleBuyInCost()) {
                throw new DoubleBuyInMismatchException("Buy-in should no be double");
            }
            if (rebuyAddOn != null && rebuyAddOn == tocConfig.getDoubleRebuyCost()) {
                throw new DoubleBuyInMismatchException("Rebuy/AddOn should not be double");
            }
        }

        if (toc != null && toc != tocConfig.getAnnualTocCost()) {
            throw new DoubleBuyInMismatchException("Annual TOC incorrect");
        }
        if (qToc != null && qToc != tocConfig.getQuarterlyTocCost()) {
            throw new DoubleBuyInMismatchException("Quarterly TOC incorrect");
        }

    }

    @Transactional
    public GamePlayer createFirstTimeGamePlayer(int gameId, FirstTimeGamePlayer firstTimeGamePlayer) {
        String firstName = firstTimeGamePlayer.getFirstName();
        String lastName = firstTimeGamePlayer.getLastName();
        Player player = Player.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(firstTimeGamePlayer.getEmail())
            .build();
        int playerId = playerRepository.save(player);

        StringBuilder name = new StringBuilder();
        name.append(!Objects.isNull(firstName) ? firstName : "");
        name.append((!Objects.isNull(firstName) && !Objects.isNull(lastName)) ? " " : "");
        name.append(!Objects.isNull(lastName) ? lastName : "");

        GamePlayer gamePlayer = GamePlayer.builder()
            .gameId(gameId)
            .playerId(playerId)
            .name(name.toString())
            .buyInCollected(firstTimeGamePlayer.getBuyInCollected())
            .annualTocCollected(firstTimeGamePlayer.getAnnualTocCollected())
            .quarterlyTocCollected(firstTimeGamePlayer.getQuarterlyTocCollected())
            .build();

        return this.createGamePlayerWorker(gamePlayer);
    }



    private void recalculate(Game game) {
        List<GamePlayer> gamePlayers = gamePlayerRepository.selectByGameId(game.getId());
        Game calculatedGame = gameCalculator.calculate(game, gamePlayers);
        payoutCalculator.calculate(calculatedGame, gamePlayers);
        pointsCalculator.calculate(calculatedGame, gamePlayers);

    }

    // Cache it
    private TocConfig getTocConfig() {
        if (tocConfig == null) {
            tocConfig = configRepository.get();
        }
        return tocConfig;
    }

}
