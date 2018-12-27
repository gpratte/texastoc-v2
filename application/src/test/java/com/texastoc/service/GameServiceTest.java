package com.texastoc.service;

import com.texastoc.TestConstants;
import com.texastoc.exception.DoubleBuyInMismatchException;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Quarter;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
public class GameServiceTest implements TestConstants {

    private GameService gameService;
    private Random random = new Random(System.currentTimeMillis());

    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private GamePlayerRepository gamePlayerRepository;
    @MockBean
    private GamePayoutRepository gamePayoutRepository;
    @MockBean
    private SeasonRepository seasonRepository;
    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;
    @MockBean
    private GameCalculator gameCalculator;
    @MockBean
    private PayoutCalculator payoutCalculator;
    @MockBean
    private PointsCalculator pointsCalculator;
    @MockBean
    private ConfigRepository configRepository;

    @Before
    public void before() {
        gameService = new GameService(gameRepository, playerRepository, gamePlayerRepository, gamePayoutRepository, seasonRepository, qSeasonRepository, gameCalculator, payoutCalculator, pointsCalculator, configRepository);
    }

    @Test
    public void testCreateGame() {

        boolean doubleBuyIn = random.nextBoolean();

        // Arrange
        LocalDate start = LocalDate.now();
        Game expected = Game.builder()
            .date(start)
            .hostId(1)
            .transportRequired(true)
            .doubleBuyIn(doubleBuyIn)
            .build();

        Mockito.when(gameRepository.save((Game) notNull())).thenReturn(1);

        Mockito.when(playerRepository.get( ArgumentMatchers.eq(1) ))
            .thenReturn(Player.builder()
                .id(1)
                .firstName("Brian")
                .lastName("Baker")
                .build());

        Mockito.when(qSeasonRepository.getCurrent())
            .thenReturn(QuarterlySeason.builder()
                .id(1)
                .quarter(Quarter.FIRST)
                .seasonId(1)
                .build());

        Mockito.when(seasonRepository.getCurrent())
            .thenReturn(Season.builder()
                .id(1)
                .kittyPerGame(KITTY_PER_GAME)
                .tocPerGame(TOC_PER_GAME)
                .quarterlyTocPerGame(QUARTERLY_TOC_PER_GAME)
                .quarterlyNumPayouts(QUARTERLY_NUM_PAYOUTS)
                .buyInCost(GAME_BUY_IN)
                .rebuyAddOnCost(GAME_REBUY)
                .rebuyAddOnTocDebit(GAME_REBUY_TOC_DEBIT)
                .doubleBuyInCost(GAME_DOUBLE_BUY_IN)
                .doubleRebuyAddOnCost(GAME_DOUBLE_REBUY)
                .doubleRebuyAddOnTocDebit(GAME_DOUBLE_REBUY_TOC_DEBIT)
                .build());

        // Act
        Game actual = gameService.createGame(expected);

        // Game repository called once
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(Game.class));

        // Game argument match
        ArgumentCaptor<Game> gameArg = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository).save(gameArg.capture());
        Assert.assertEquals(start, gameArg.getValue().getDate());
        Assert.assertEquals(1, (int)gameArg.getValue().getHostId());
        Assert.assertTrue(gameArg.getValue().getTransportRequired());
        Assert.assertEquals(doubleBuyIn, gameArg.getValue().getDoubleBuyIn());


        // Assert
        Assert.assertNotNull("new game should not be null", actual);
        Assert.assertEquals("new game id should be 1", 1, (int)actual.getId());

        Assert.assertEquals("SeasonId should be 1", 1, (int)actual.getSeasonId());
        Assert.assertEquals("QuarterlySeasonId should be 1", 1, (int)actual.getQSeasonId());
        Assert.assertEquals("Host id should be 1", expected.getHostId(), actual.getHostId());
        Assert.assertEquals("date should be now", expected.getDate(), actual.getDate());

        Assert.assertTrue("Host name should be Brian Baker", "Brian Baker".equals(actual.getHostName()));
        Assert.assertEquals("Quarter should be first", Quarter.FIRST, actual.getQuarter());

        Assert.assertNull("last calculated should be null", actual.getLastCalculated());


        // Game setup variables
        Assert.assertEquals("Double buy in", expected.getDoubleBuyIn(), actual.getDoubleBuyIn());
        Assert.assertEquals("transport required", expected.getTransportRequired(), actual.getTransportRequired());
        Assert.assertEquals("Kitty cost should be amount set for season", KITTY_PER_GAME, (int)actual.getKittyCost());
        Assert.assertEquals("Annual TOC be amount set for season", TOC_PER_GAME, (int)actual.getAnnualTocCost());
        Assert.assertEquals("Quarterly TOC be amount set for season", QUARTERLY_TOC_PER_GAME, (int)actual.getQuarterlyTocCost());

        Assert.assertNull("not started", actual.getStarted());

        Assert.assertEquals("No players", 0, (int)actual.getNumPlayers());
        Assert.assertEquals("No kitty collected", 0, (int)actual.getKittyCollected());
        Assert.assertEquals("No buy in collected", 0, (int)actual.getBuyInCollected());
        Assert.assertEquals("No rebuy collected", 0, (int)actual.getRebuyAddOnCollected());
        Assert.assertEquals("No annual toc collected", 0, (int)actual.getAnnualTocCollected());
        Assert.assertEquals("No quarterly toc collected", 0, (int)actual.getQuarterlyTocCollected());

        Assert.assertFalse("not finalized", actual.getFinalized());

        if (expected.getDoubleBuyIn()) {
            Assert.assertEquals("Buy in cost should be double the amount set for season", GAME_DOUBLE_BUY_IN, (int)actual.getBuyInCost());
            Assert.assertEquals("Rebuy cost should be double the amount set for season", GAME_DOUBLE_REBUY, (int)actual.getRebuyAddOnCost());
            Assert.assertEquals("Rebuy Toc debit cost should be double the amount set for season", GAME_DOUBLE_REBUY_TOC_DEBIT, (int)actual.getRebuyAddOnTocDebit());
        } else {
            Assert.assertEquals("Buy in cost should be amount set for season", GAME_BUY_IN, (int)actual.getBuyInCost());
            Assert.assertEquals("Rebuy cost should be amount set for season", GAME_REBUY, (int)actual.getRebuyAddOnCost());
            Assert.assertEquals("Rebuy Toc debit cost should be amount set for season", GAME_REBUY_TOC_DEBIT, (int)actual.getRebuyAddOnTocDebit());
        }
    }

    /**
     * Somewhat of an anorexic test since there are no players but then again
     * the game service code is just a pass through to the repositories.
     */
    @Test
    public void getGameNoPlayers() {

        Mockito.when(gameRepository.getById(1))
            .thenReturn(Game.builder()
                .id(1)
                .build());

        Mockito.when(gamePlayerRepository.selectByGameId(1))
            .thenReturn(Collections.emptyList());

        Mockito.when(gamePayoutRepository.getByGameId(1))
            .thenReturn(Collections.emptyList());

        Game game = gameService.getGame(1);

        // Game repository called once
        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Assert.assertNotNull("Game returned should not be null", game);
        Assert.assertEquals("Game id should be 1", 1, (int)game.getId());

        // GamePlayer repository called once
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Assert.assertNotNull("GamePlayers returned should not be null", game.getPlayers());
        Assert.assertEquals("number of players should be 0", 0, game.getPlayers().size());

        // GamePayout repository called once
        Mockito.verify(gamePayoutRepository, Mockito.times(1)).getByGameId(1);
        Assert.assertNotNull("GamePayouts returned should not be null", game.getPayouts());
        Assert.assertEquals("number of payouts should be 0", 0, game.getPayouts().size());

    }

    @Test
    public void testUpdateGame() {

        Game game = Game.builder()
            .id(1)
            .build();

        Mockito.doNothing().when(gameRepository).update((Game) notNull());

        gameService.updateGame(game);

        Mockito.verify(gameRepository, Mockito.times(1)).update(Mockito.any(Game.class));
    }

    /**
     * Buy-in amount does not match the buy-in required for the game
     */
    @Test(expected = DoubleBuyInMismatchException.class)
    public void testCreateGamePlayerBuyInWrong() {

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        boolean doubleBuyIn = random.nextBoolean();

        Game currentGame = Game.builder()
            .numPlayers(0)
            .doubleBuyIn(doubleBuyIn)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .buyInCollected(doubleBuyIn ? GAME_BUY_IN : GAME_DOUBLE_BUY_IN)
            .build();

        gameService.createGamePlayer(gamePlayerToCreate);

        Assert.fail("Should have thrown an exception");
    }

    /**
     * Annual TOC amount wrong
     */
    @Test(expected = DoubleBuyInMismatchException.class)
    public void testCreateGamePlayerTocWrong() {

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Game currentGame = Game.builder()
            .numPlayers(0)
            .doubleBuyIn(false)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .annualTocCollected(TOC_PER_GAME + 1)
            .build();

        gameService.createGamePlayer(gamePlayerToCreate);

        Assert.fail("Should have thrown an exception");
    }

    @Test
    public void testCreateGamePlayer() {

        // GameService#createGamePlayers calls
        // 1. gamePlayerRepository.save
        // 2. gameRepository.getById
        // 3. gamePlayerRepository.selectByGameId
        // 4. gameCalculator.calculate
        // 5. payoutCalculator.calculate
        // 6. pointsCalculator.calculate
        // Not verifying the calculators because they have their own tests

        Mockito.when(gamePlayerRepository.save((GamePlayer) notNull())).thenReturn(1);

        Game currentGame = Game.builder()
            .id(1)
            .numPlayers(0)
            .doubleBuyIn(false)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        String playerName = Long.toString(System.currentTimeMillis());
        GamePlayer gamePlayerToCreated = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .name(playerName)
            .build();
        List<GamePlayer> gamePlayersCreated = new LinkedList<>();
        gamePlayersCreated.add(gamePlayerToCreated);
        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(gamePlayersCreated);

        Game calculatedGame = Game.builder()
            .numPlayers(1)
            .prizePot(0)
            .build();

        Mockito.when(gameCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(calculatedGame);
        Mockito.when(payoutCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(pointsCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(Collections.EMPTY_LIST);

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .name(playerName)
            .build();

        GamePlayer gamePlayerCreated = gameService.createGamePlayer(gamePlayerToCreate);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).save(Mockito.any(GamePlayer.class));
        ArgumentCaptor<GamePlayer> gamePlayerArg = ArgumentCaptor.forClass(GamePlayer.class);
        Mockito.verify(gamePlayerRepository).save(gamePlayerArg.capture());
        Assert.assertEquals(1, gamePlayerArg.getValue().getGameId());
        Assert.assertEquals(1, gamePlayerArg.getValue().getPlayerId());
        Assert.assertEquals(playerName, gamePlayerArg.getValue().getName());

        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Mockito.verify(gameCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());
        Mockito.verify(payoutCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());
        Mockito.verify(pointsCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());


        Assert.assertNotNull("game player created should not be null", gamePlayerCreated);
        Assert.assertEquals("game player id should be 1", 1, gamePlayerCreated.getGameId());
        Assert.assertEquals("game player id should be 1", 1, gamePlayerCreated.getPlayerId());
        Assert.assertEquals("game player name should be " + playerName, playerName, gamePlayerCreated.getName());

        Assert.assertNull("game player points should be null", gamePlayerCreated.getPoints());

        Assert.assertNull("game player finish should be null", gamePlayerCreated.getFinish());
        Assert.assertNull("game player knocked out should be null", gamePlayerCreated.getKnockedOut());
        Assert.assertNull("game player round updates should be null", gamePlayerCreated.getRoundUpdates());
        Assert.assertNull("game player buy-in collected should be null", gamePlayerCreated.getBuyInCollected());
        Assert.assertNull("game player rebuy add on collected should be null", gamePlayerCreated.getRebuyAddOnCollected());
        Assert.assertNull("game player annual toc collected should be null", gamePlayerCreated.getAnnualTocCollected());
        Assert.assertNull("game player quarterly toc collected should be null", gamePlayerCreated.getQuarterlyTocCollected());
        Assert.assertNull("game player chop should be null", gamePlayerCreated.getChop());
    }

    /**
     * Rebuy amount does not match the rebuy required for the game
     */
    @Test(expected = DoubleBuyInMismatchException.class)
    public void testUpdateGamePlayerRebuyWrong() {

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        boolean doubleBuyIn = random.nextBoolean();

        Game currentGame = Game.builder()
            .numPlayers(0)
            .doubleBuyIn(doubleBuyIn)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        Mockito.doNothing().when(gamePlayerRepository).update((GamePlayer) notNull());

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .buyInCollected(doubleBuyIn ? GAME_DOUBLE_BUY_IN : GAME_BUY_IN)
            .rebuyAddOnCollected(doubleBuyIn ? GAME_REBUY : GAME_DOUBLE_REBUY)
            .build();

        gameService.createGamePlayer(gamePlayerToCreate);

        Assert.fail("Should have thrown an exception");
    }

    /**
     * Quarterly TOC amount wrong
     */
    @Test(expected = DoubleBuyInMismatchException.class)
    public void testCreateGamePlayerQtocWrong() {

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Game currentGame = Game.builder()
            .numPlayers(0)
            .doubleBuyIn(false)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .gameId(1)
            .playerId(1)
            .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME - 1)
            .build();

        gameService.createGamePlayer(gamePlayerToCreate);

        Assert.fail("Should have thrown an exception");
    }

    @Test
    public void testUpdateGamePlayer() {

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Game currentGame = Game.builder()
            .id(1)
            .numPlayers(1)
            .doubleBuyIn(false)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        GamePlayer gamePlayer = GamePlayer.builder()
            .id(1)
            .gameId(1)
            .buyInCollected(GAME_BUY_IN)
            .rebuyAddOnCollected(GAME_REBUY)
            .annualTocCollected(TOC_PER_GAME)
            .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
            .roundUpdates(true)
            .finish(10)
            .knockedOut(true)
            .chop(500)
            .build();

        List<GamePlayer> gamePlayersCreated = new LinkedList<>();
        gamePlayersCreated.add(gamePlayer);
        Mockito.when(gamePlayerRepository.selectByGameId(1)).thenReturn(gamePlayersCreated);

        Game calculatedGame = Game.builder()
            .numPlayers(1)
            .prizePot(0)
            .build();
        Mockito.when(gameCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(calculatedGame);
        Mockito.when(payoutCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(pointsCalculator.calculate((Game) notNull(), (List<GamePlayer>) notNull())).thenReturn(Collections.EMPTY_LIST);

        gameService.updateGamePlayer(gamePlayer);

        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).update(Mockito.any(GamePlayer.class));
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Mockito.verify(gameCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());
        Mockito.verify(payoutCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());
        Mockito.verify(pointsCalculator, Mockito.times(1)).calculate(Mockito.any(Game.class), Mockito.anyList());
    }

    @Test
    public void testDeleteGamePlayer() {

        GamePlayer gamePlayer = GamePlayer.builder()
            .id(1)
            .gameId(1)
            .build();
        Mockito.when(gamePlayerRepository.selectById(1)).thenReturn(gamePlayer);

        Mockito.doNothing().when(gamePlayerRepository).deleteById(1);

        Game currentGame = Game.builder()
            .id(1)
            .numPlayers(0)
            .doubleBuyIn(false)
            .build();
        Mockito.when(gameRepository.getById(1)).thenReturn(currentGame);

        gameService.deleteGamePlayer(1);

        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).deleteById(1);
        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
    }

}
