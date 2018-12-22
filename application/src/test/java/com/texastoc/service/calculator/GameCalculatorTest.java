package com.texastoc.service.calculator;

import com.texastoc.TestConstants;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
public class GameCalculatorTest implements TestConstants {

    private GameCalculator gameCalculator;

    private Random random = new Random(System.currentTimeMillis());

    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private GamePlayerRepository gamePlayerRepository;
    @MockBean
    private ConfigRepository configRepository;

    @Before
    public void before() {
        gameCalculator = new GameCalculator(gameRepository, gamePlayerRepository, configRepository);
    }

    @Test
    public void testNoGamePlayers() {

        Mockito.when(gameRepository.getById(1))
            .thenReturn(Game.builder()
                .id(1)
                .numPlayers(0)
                .kittyCollected(0)
                .buyInCollected(0)
                .rebuyAddOnCollected(0)
                .annualTocCollected(0)
                .quarterlyTocCollected(0)
                .finalized(false)
                .lastCalculated(LocalDateTime.now())
                .build());

        Mockito.when(gamePlayerRepository.selectByGameId(1))
            .thenReturn(Collections.emptyList());

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Mockito.doNothing().when(gameRepository).update((Game) notNull());

        LocalDateTime started = LocalDateTime.now();
        Game gameCalculated = gameCalculator.calculate(1);

        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Mockito.verify(configRepository, Mockito.times(1)).get();
        Mockito.verify(gameRepository, Mockito.times(1)).update(Mockito.any(Game.class));

        Assert.assertNotNull("game calculated should not be null", gameCalculated);

        Assert.assertEquals("number of game players should be 0", 0, (int) gameCalculated.getNumPlayers());
        Assert.assertEquals("kitty collected should be 0", 0, (int) gameCalculated.getKittyCollected());
        Assert.assertEquals("buy-in collected should be 0", 0, (int) gameCalculated.getBuyInCollected());
        Assert.assertEquals("rebuy add on collected should be 0", 0, (int) gameCalculated.getRebuyAddOnCollected());
        Assert.assertEquals("annual toc collected should be 0", 0, (int) gameCalculated.getAnnualTocCollected());
        Assert.assertEquals("quarterly toc collected should be 0", 0, (int) gameCalculated.getQuarterlyTocCollected());
        Assert.assertFalse("not finalized", gameCalculated.getFinalized());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastCalculated = gameCalculated.getLastCalculated();
        boolean isBetweenStartAndNow = started.isBefore(lastCalculated) && now.isAfter(lastCalculated);
        Assert.assertTrue("last calculated should be between start and now", isBetweenStartAndNow);
    }

    @Test
    public void testGamePlayersNoBuyIns() {

        Mockito.when(gameRepository.getById(1))
            .thenReturn(Game.builder()
                .id(1)
                .numPlayers(0)
                .kittyCollected(0)
                .buyInCollected(0)
                .rebuyAddOnCollected(0)
                .annualTocCollected(0)
                .quarterlyTocCollected(0)
                .finalized(false)
                .lastCalculated(LocalDateTime.now())
                .build());

        List<GamePlayer> gamePlayers = new ArrayList<>();
        int playersToCreate = random.nextInt(10);
        for (int i = 0; i < playersToCreate; ++i) {
            GamePlayer gamePlayer = GamePlayer.builder()
                .id(i)
                .playerId(i)
                .gameId(1)
                .build();
            gamePlayers.add(gamePlayer);
        }

        Mockito.when(gamePlayerRepository.selectByGameId(1))
            .thenReturn(gamePlayers);

        System.out.println("!!! " + TestConstants.getTocConfig());
        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Mockito.doNothing().when(gameRepository).update((Game) notNull());

        Game gameCalculated = gameCalculator.calculate(1);

        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Mockito.verify(configRepository, Mockito.times(0)).get();
        Mockito.verify(gameRepository, Mockito.times(1)).update(Mockito.any(Game.class));

        Assert.assertNotNull("game calculated should not be null", gameCalculated);

        Assert.assertEquals("number of game players should be " + playersToCreate, playersToCreate, (int) gameCalculated.getNumPlayers());
        Assert.assertEquals("kitty collected should be 0", 0, (int) gameCalculated.getKittyCollected());
        Assert.assertEquals("buy-in collected should be 0", 0, (int) gameCalculated.getBuyInCollected());
        Assert.assertEquals("rebuy add on collected should be 0", 0, (int) gameCalculated.getRebuyAddOnCollected());
        Assert.assertEquals("annual toc collected should be 0", 0, (int) gameCalculated.getAnnualTocCollected());
        Assert.assertEquals("quarterly toc collected should be 0", 0, (int) gameCalculated.getQuarterlyTocCollected());

    }

    /**
     * One of each means
     * <ul>
     * <li>buy in, no annual toc, no quarterly toc</li>
     * <li>buy in, annual toc, no quarterly toc</li>
     * <li>buy in, annual toc, quarterly toc</li>
     * </ul>
     */
    @Test
    public void testGamePlayerOneOfEach() {

        Mockito.when(gameRepository.getById(1))
            .thenReturn(Game.builder()
                .id(1)
                .numPlayers(0)
                .kittyCollected(0)
                .buyInCollected(0)
                .rebuyAddOnCollected(0)
                .annualTocCollected(0)
                .quarterlyTocCollected(0)
                .finalized(false)
                .lastCalculated(LocalDateTime.now())
                .build());

        List<GamePlayer> gamePlayers = new ArrayList<>();
        GamePlayer gamePlayer = GamePlayer.builder()
            .id(1)
            .playerId(1)
            .gameId(1)
            .buyInCollected(GAME_BUY_IN)
            .build();
        gamePlayers.add(gamePlayer);

        gamePlayer = GamePlayer.builder()
            .id(2)
            .playerId(2)
            .gameId(1)
            .buyInCollected(GAME_BUY_IN)
            .annualTocCollected(TestConstants.TOC_PER_GAME)
            .build();
        gamePlayers.add(gamePlayer);

        gamePlayer = GamePlayer.builder()
            .id(2)
            .playerId(2)
            .gameId(1)
            .buyInCollected(GAME_BUY_IN)
            .annualTocCollected(TOC_PER_GAME)
            .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
            .build();
        gamePlayers.add(gamePlayer);

        Mockito.when(gamePlayerRepository.selectByGameId(1))
            .thenReturn(gamePlayers);

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        Game gameCalculated = gameCalculator.calculate(1);

        Mockito.verify(gameRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectByGameId(1);
        Mockito.verify(configRepository, Mockito.times(1)).get();

        Assert.assertNotNull("game calculated should not be null", gameCalculated);

        Assert.assertEquals("number of game players should be 0", 0, (int) gameCalculated.getNumPlayers());
        Assert.assertEquals("kitty collected should be 0", 0, (int) gameCalculated.getKittyCollected());
        Assert.assertEquals("buy-in collected should be 0", 0, (int) gameCalculated.getBuyInCollected());
        Assert.assertEquals("rebuy add on collected should be 0", 0, (int) gameCalculated.getRebuyAddOnCollected());
        Assert.assertEquals("annual toc collected should be 0", 0, (int) gameCalculated.getAnnualTocCollected());
        Assert.assertEquals("quarterly toc collected should be 0", 0, (int) gameCalculated.getQuarterlyTocCollected());

    }

}
