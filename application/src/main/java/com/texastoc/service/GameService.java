package com.texastoc.service;

import com.texastoc.connector.SMSConnector;
import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.exception.DoubleBuyInChangeDisallowedException;
import com.texastoc.exception.GameInProgressException;
import com.texastoc.exception.GameIsFinalizedException;
import com.texastoc.model.config.TocConfig;
import com.texastoc.model.game.*;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.*;
import com.texastoc.service.calculator.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class GameService {

  private final RoleRepository roleRepository;
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

  private final SMSConnector smsConnector;

  private TocConfig tocConfig;

  public GameService(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, GamePayoutRepository gamePayoutRepository, SeasonRepository seasonRepository, QuarterlySeasonRepository qSeasonRepository, GameCalculator gameCalculator, PayoutCalculator payoutCalculator, PointsCalculator pointsCalculator, ConfigRepository configRepository, SeasonCalculator seasonCalculator, QuarterlySeasonCalculator qSeasonCalculator, SeatingRepository seatingRepository, RoleRepository roleRepository, SMSConnector smsConnector) {
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
    this.roleRepository = roleRepository;
    this.smsConnector = smsConnector;
  }

  @Transactional
  public Game createGame(Game game) {
    // Reject if there is a game in progress
    Game currentGame = getCurrentGame();
    if (currentGame != null && !currentGame.isFinalized()) {
      throw new GameInProgressException("There is a game in progress.");
    }

    Game gameToCreate = new Game();

    // TODO check that date is allowed - not before an existing game and not beyond the season.
    gameToCreate.setDate(game.getDate());

    // TODO exception if null
    QuarterlySeason currentQSeason = qSeasonRepository.getCurrent();
    gameToCreate.setSeasonId(currentQSeason.getSeasonId());
    gameToCreate.setQSeasonId(currentQSeason.getId());
    gameToCreate.setQuarter(currentQSeason.getQuarter());
    gameToCreate.setQuarterlyGameNum(currentQSeason.getNumGamesPlayed() + 1);

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
    gameToCreate.setSeasonGameNum(currentSeason.getNumGamesPlayed() + 1);

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

    return populateGame(gameToCreate);
  }

  @Transactional(readOnly = true)
  public Game getGame(int id) {
    Game game = gameRepository.getById(id);
    return populateGame(game);
  }

  @Transactional(readOnly = true)
  public Game getCurrentGame() {
    int seasonId = seasonRepository.getCurrent().getId();
    List<Game> games = gameRepository.getMostRecent(seasonId);
    if (games.size() > 0) {
      Game game = games.get(0);
      populateGame(game);
      return game;
    }

    return null;
  }

  public void notifySeating(int gameId) {
    Seating seating = seatingRepository.get(gameId);
    if (seating == null || seating.getTables() == null || seating.getTables().size() == 0) {
      return;
    }
    for (Table table : seating.getTables()) {
      if (table.getSeats() == null || table.getSeats().size() == 0) {
        continue;
      }
      for (Seat seat : table.getSeats()) {
        if (seat == null) {
          continue;
        }
        GamePlayer gamePlayer = gamePlayerRepository.selectById(seat.getGamePlayerId());
        Player player = playerRepository.get(gamePlayer.getPlayerId());
        if (player.getPhone() != null) {
          smsConnector.text(player.getPhone(), player.getName() + " table " +
            table.getNumber() + " seat " + seat.getSeatNumber());
        }
      }
    }
  }

  private Game populateGame(Game game) {
    List<GamePlayer> players = gamePlayerRepository.selectByGameId(game.getId());
    game.setPlayers(players);
    int numPaidPlayers = 0;
    int numPaidPlayersRemaining = 0;
    for (GamePlayer player : players) {
      if (player.getBuyInCollected() != null && player.getBuyInCollected() > 0) {
        ++numPaidPlayers;
        if (player.getKnockedOut() == null || !player.getKnockedOut()) {
          ++numPaidPlayersRemaining;
        }
      }
    }
    game.setNumPaidPlayers(numPaidPlayers);
    game.setNumPaidPlayersRemaining(numPaidPlayersRemaining);

    game.setPayouts(gamePayoutRepository.getByGameId(game.getId()));

    Seating seating = new Seating();
    try {
      seating = seatingRepository.get(game.getId());
    } catch (Exception e) {
      // do nothing
    }
    game.setSeating(seating);
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
  public GamePlayer createGamePlayer(CreateGamePlayerRequest cgpr) {
    Game game = gameRepository.getById(cgpr.getGameId());
    checkFinalized(game);

    GamePlayer gamePlayer = GamePlayer.builder()
      .playerId(cgpr.getPlayerId())
      .gameId(cgpr.getGameId())
      .buyInCollected(cgpr.isBuyInCollected() ? game.getBuyInCost() : null)
      .annualTocCollected(cgpr.isAnnualTocCollected() ? game.getAnnualTocCost() : null)
      .quarterlyTocCollected(cgpr.isQuarterlyTocCollected() ? game.getQuarterlyTocCost() : null)
      .build();

    return createGamePlayerWorker(gamePlayer, game);
  }

  @Transactional
  public GamePlayer updateGamePlayer(UpdateGamePlayerRequest ugpr) {
    Game game = gameRepository.getById(ugpr.getGameId());
    checkFinalized(game);

    Integer place = ugpr.getPlace();
    GamePlayer gamePlayer = getGamePlayer(ugpr.getGamePlayerId());
    gamePlayer.setPlace(place);
    gamePlayer.setRoundUpdates(ugpr.isRoundUpdates());
    gamePlayer.setBuyInCollected(ugpr.isBuyInCollected() ? game.getBuyInCost() : null);
    gamePlayer.setRebuyAddOnCollected(ugpr.isRebuyAddOnCollected() ? game.getRebuyAddOnCost() : null);
    gamePlayer.setAnnualTocCollected(ugpr.isAnnualTocCollected() ? game.getAnnualTocCost() : null);
    gamePlayer.setQuarterlyTocCollected(ugpr.isQuarterlyTocCollected() ? game.getQuarterlyTocCost() : null);
    gamePlayer.setChop(ugpr.getChop());

    if (place != null && place <= 10) {
      gamePlayer.setKnockedOut(true);
    } else {
      gamePlayer.setKnockedOut(ugpr.isKnockedOut());
    }

    // TODO no more double buy in
    // Make sure money is right
    verifyGamePlayerMoney(game.isDoubleBuyIn(), gamePlayer);

    gamePlayerRepository.update(gamePlayer);

    recalculate(game);

    return gamePlayer;
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
    Game game = gameRepository.getById(firstTimeGamePlayer.getGameId());
    checkFinalized(game);

    String firstName = firstTimeGamePlayer.getFirstName();
    String lastName = firstTimeGamePlayer.getLastName();
    Player player = Player.builder()
      .firstName(firstName)
      .lastName(lastName)
      .email(firstTimeGamePlayer.getEmail())
      .build();
    int playerId = playerRepository.save(player);
    roleRepository.save(playerId);

    StringBuilder name = new StringBuilder();
    name.append(!Objects.isNull(firstName) ? firstName : "");
    name.append((!Objects.isNull(firstName) && !Objects.isNull(lastName)) ? " " : "");
    name.append(!Objects.isNull(lastName) ? lastName : "");

    GamePlayer gamePlayer = GamePlayer.builder()
      .gameId(firstTimeGamePlayer.getGameId())
      .playerId(playerId)
      .name(name.toString())
      .buyInCollected(firstTimeGamePlayer.isBuyInCollected() ? game.getBuyInCost() : null)
      .annualTocCollected(firstTimeGamePlayer.isAnnualTocCollected() ? game.getAnnualTocCost() : null)
      .quarterlyTocCollected(firstTimeGamePlayer.isQuarterlyTocCollected() ? game.getQuarterlyTocCost() : null)
      .build();

    return createGamePlayerWorker(gamePlayer, game);
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

  public void openGame(int id) {
    Game game = gameRepository.getById(id);

    Season season = seasonRepository.get(game.getSeasonId());
    if (season.isFinalized()) {
      // TODO throw exception and handle in RestControllerAdvise
      return;
    }

    game.setFinalized(false);
    gameRepository.update(game);
  }

  // Worker to avoid one @Transacation calling anther @Transactional
  private GamePlayer createGamePlayerWorker(GamePlayer gamePlayer, Game game) {

    // Make sure money is right
    verifyGamePlayerMoney(game.isDoubleBuyIn(), gamePlayer);

    if (gamePlayer.getName() == null) {
      Player player = playerRepository.get(gamePlayer.getPlayerId());
      gamePlayer.setName(player.getName());
    }
    gamePlayer.setQSeasonId(game.getQSeasonId());
    gamePlayer.setSeasonId(game.getSeasonId());

    int gamePlayerId = gamePlayerRepository.save(gamePlayer);
    gamePlayer.setId(gamePlayerId);

    recalculate(game);

    return gamePlayer;
  }

  // TODO separate thread
  private void recalculate(Game game) {
    List<GamePlayer> gamePlayers = gamePlayerRepository.selectByGameId(game.getId());
    Game calculatedGame = gameCalculator.calculate(game, gamePlayers);
    payoutCalculator.calculate(calculatedGame, gamePlayers);
    pointsCalculator.calculate(calculatedGame, gamePlayers);
  }

  // TODO fix this now that there is no double buyin
  private void verifyGamePlayerMoney(boolean doubleBuyIn, GamePlayer gamePlayer) {
//    TocConfig tocConfig = getTocConfig();
//    Integer buyIn = gamePlayer.getBuyInCollected();
//    Integer rebuyAddOn = gamePlayer.getRebuyAddOnCollected();
//    Integer toc = gamePlayer.getAnnualTocCollected();
//    Integer qToc = gamePlayer.getQuarterlyTocCollected();
//
//    if (doubleBuyIn) {
//      if (buyIn != null && buyIn != tocConfig.getDoubleBuyInCost()) {
//        throw new DoubleBuyInMismatchException("Buy-in should be double");
//      }
//      if (rebuyAddOn != null && rebuyAddOn != tocConfig.getDoubleRebuyCost()) {
//        throw new DoubleBuyInMismatchException("Rebuy/AddOn should be double");
//      }
//    } else {
//      if (buyIn != null && buyIn == tocConfig.getDoubleBuyInCost()) {
//        throw new DoubleBuyInMismatchException("Buy-in should no be double");
//      }
//      if (rebuyAddOn != null && rebuyAddOn == tocConfig.getDoubleRebuyCost()) {
//        throw new DoubleBuyInMismatchException("Rebuy/AddOn should not be double");
//      }
//    }
//
//    if (toc != null && toc != tocConfig.getAnnualTocCost()) {
//      throw new DoubleBuyInMismatchException("Annual TOC incorrect");
//    }
//    if (qToc != null && qToc != tocConfig.getQuarterlyTocCost()) {
//      throw new DoubleBuyInMismatchException("Quarterly TOC incorrect");
//    }

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
      throw new GameIsFinalizedException("Game is finalized");
    }
  }
}
