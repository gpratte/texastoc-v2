package com.texastoc.service;

import com.texastoc.exception.DoubleBuyInChangeDisallowedException;
import com.texastoc.exception.DoubleBuyInMismatchException;
import com.texastoc.exception.FinalizedException;
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
import com.texastoc.repository.SeatingRepository;
import com.texastoc.service.calculator.GameCalculator;
import com.texastoc.service.calculator.PayoutCalculator;
import com.texastoc.service.calculator.PointsCalculator;
import com.texastoc.service.calculator.QuarterlySeasonCalculator;
import com.texastoc.service.calculator.SeasonCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SeasonRepository seasonRepository;
    private final SeatingRepository seatingRepository;
    private final PlayerRepository playerRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GamePayoutRepository gamePayoutRepository;
    private final QuarterlySeasonRepository qSeasonRepository;

    private final GameCalculator gameCalculator;
    private final PayoutCalculator payoutCalculator;
    private final PointsCalculator pointsCalculator;
    private final ConfigRepository configRepository;
    private final SeasonCalculator seasonCalculator;
    private final QuarterlySeasonCalculator qSeasonCalculator;

    private TocConfig tocConfig;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameCalculator gameCalculator, PayoutCalculator payoutCalculator, PointsCalculator pointsCalculator, ConfigRepository configRepository, SeasonCalculator seasonCalculator, QuarterlySeasonCalculator qSeasonCalculator, SeatingRepository seatingRepository) {
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
        this.seasonCalculator = seasonCalculator;
        this.qSeasonCalculator = qSeasonCalculator;
        this.seatingRepository = seatingRepository;
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
        gameToCreate.setDoubleBuyIn(game.isDoubleBuyIn());
        gameToCreate.setTransportRequired(game.isTransportRequired());

        Season currentSeason = seasonRepository.getCurrent();
        gameToCreate.setKittyCost(currentSeason.getKittyPerGame());
        gameToCreate.setBuyInCost(currentSeason.getBuyInCost());
        gameToCreate.setRebuyAddOnCost(currentSeason.getRebuyAddOnCost());
        gameToCreate.setRebuyAddOnTocDebit(currentSeason.getRebuyAddOnTocDebit());
        gameToCreate.setAnnualTocCost(currentSeason.getTocPerGame());
        gameToCreate.setQuarterlyTocCost(currentSeason.getQuarterlyTocPerGame());

        if (game.isDoubleBuyIn()) {
            gameToCreate.setBuyInCost(currentSeason.getDoubleBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getDoubleRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getDoubleRebuyAddOnTocDebit());
        } else {
            gameToCreate.setBuyInCost(currentSeason.getBuyInCost());
            gameToCreate.setRebuyAddOnCost(currentSeason.getRebuyAddOnCost());
            gameToCreate.setRebuyAddOnTocDebit(currentSeason.getRebuyAddOnTocDebit());
        }

        int id = gameRepository.save(gameToCreate);
        gameToCreate.setId(id);

        return gameToCreate;
    }

    @Transactional(readOnly = true)
    public Game getGame(int id) {
        Game game = gameRepository.getById(id);
        game.setPlayers(gamePlayerRepository.selectByGameId(id));
        game.setPayouts(gamePayoutRepository.getByGameId(id));
        game.setTables(seatingRepository.getTables(id));
        return game;
    }

    @Transactional
    public void updateGame(Game game) {
        Game currentGame = gameRepository.getById(game.getId());
        checkFinalized(currentGame);

        // Do not allow the game double buy-in to change if any players have bought in
        if (currentGame.isDoubleBuyIn() != game.isDoubleBuyIn()) {
            List<GamePlayer> gamePlayers = gamePlayerRepository.selectByGameId(game.getId());
            for (GamePlayer gamePlayer : gamePlayers) {
                if (gamePlayer.getBuyInCollected() != null && gamePlayer.getBuyInCollected() > 0) {
                    throw new DoubleBuyInChangeDisallowedException();
                }
            }
        }

        gameRepository.update(game);
    }

    @Transactional
    public GamePlayer createGamePlayer(GamePlayer gamePlayer) {
        checkFinalized(gamePlayer.getGameId());
        return createGamePlayerWorker(gamePlayer);
    }

    @Transactional
    public void updateGamePlayer(GamePlayer gamePlayer) {
        checkFinalized(gamePlayer.getGameId());

        int gameId = gamePlayer.getGameId();
        Game currentGame = gameRepository.getById(gameId);

        // Make sure money is right
        verifyGamePlayerMoney(currentGame.isDoubleBuyIn(), gamePlayer);

        gamePlayerRepository.update(gamePlayer);

        recalculate(currentGame);
    }

    @Transactional
    public void deleteGamePlayer(int gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepository.selectById(gamePlayerId);
        checkFinalized(gamePlayer.getGameId());

        gamePlayerRepository.deleteById(gamePlayer.getId());

        Game currentGame = gameRepository.getById(gamePlayer.getGameId());
        recalculate(currentGame);
    }

    @Transactional(readOnly = true)
    public GamePlayer getGamePlayer(int gamePlayerId) {
        return gamePlayerRepository.selectById(gamePlayerId);
    }

    @Transactional
    public GamePlayer createFirstTimeGamePlayer(FirstTimeGamePlayer firstTimeGamePlayer) {
        checkFinalized(firstTimeGamePlayer.getGameId());

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
            .gameId(firstTimeGamePlayer.getGameId())
            .playerId(playerId)
            .name(name.toString())
            .buyInCollected(firstTimeGamePlayer.getBuyInCollected())
            .annualTocCollected(firstTimeGamePlayer.getAnnualTocCollected())
            .quarterlyTocCollected(firstTimeGamePlayer.getQuarterlyTocCollected())
            .build();

        return this.createGamePlayerWorker(gamePlayer);
    }

    public void endGame(int id) {
        Game game = gameRepository.getById(id);
        game.setFinalized(true);
        gameRepository.update(game);
        // Do not need to recalculate game b/c that is all done by other methods
        qSeasonCalculator.calculate(game.getQSeasonId());
        seasonCalculator.calculate(game.getSeasonId());
        seatingRepository.deleteByGameId(id);
    }

    // Worker to avoid one @Transacation calling anther @Transactional
    private GamePlayer createGamePlayerWorker(GamePlayer gamePlayer) {

        int gameId = gamePlayer.getGameId();
        Game currentGame = gameRepository.getById(gameId);

        // Make sure money is right
        verifyGamePlayerMoney(currentGame.isDoubleBuyIn(), gamePlayer);

        if (gamePlayer.getName() == null) {
            Player player = playerRepository.get(gamePlayer.getPlayerId());
            gamePlayer.setName(player.getName());
        }
        gamePlayer.setQSeasonId(currentGame.getQSeasonId());
        gamePlayer.setSeasonId(currentGame.getSeasonId());

        int gamePlayerId = gamePlayerRepository.save(gamePlayer);
        gamePlayer.setId(gamePlayerId);

        recalculate(currentGame);

        return gamePlayer;
    }

    private void recalculate(Game game) {
        List<GamePlayer> gamePlayers = gamePlayerRepository.selectByGameId(game.getId());
        Game calculatedGame = gameCalculator.calculate(game, gamePlayers);
        payoutCalculator.calculate(calculatedGame, gamePlayers);
        pointsCalculator.calculate(calculatedGame, gamePlayers);
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

    // Cache it
    private TocConfig getTocConfig() {
        if (tocConfig == null) {
            tocConfig = configRepository.get();
        }
        return tocConfig;
    }

    private void checkFinalized(int id) {
        checkFinalized(gameRepository.getById(id));
    }

    private void checkFinalized(Game game) {
        if (game.isFinalized()) {
            throw new FinalizedException("Game is finalized");
        }
    }

}
