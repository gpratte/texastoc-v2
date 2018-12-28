package com.texastoc.service.calculator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.texastoc.TestConstants;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import com.texastoc.model.season.SeasonPlayer;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.SeasonPlayerRepository;
import com.texastoc.repository.SeasonRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
public class SeasonCalculatorTest implements TestConstants {

    private GameCalculator gameCalculator;
    private SeasonCalculator seasonCalculator;

    private Random random = new Random(System.currentTimeMillis());

    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private ConfigRepository configRepository;
    @MockBean
    private SeasonRepository seasonRepository;
    @MockBean
    private SeasonPlayerRepository seasonPlayerRepository;

    @Before
    public void before() {
        seasonCalculator = new SeasonCalculator(gameRepository, seasonRepository, seasonPlayerRepository);
        gameCalculator = new GameCalculator(gameRepository, configRepository);
    }

    @Test
    public void testNoGames() {

        Season currentSeason = Season.builder()
            .id(1)
            .build();
        Mockito.when(seasonRepository.get(1)).thenReturn(currentSeason);

        Mockito.when(gameRepository.getBySeasonId(1)).thenReturn(Collections.emptyList());

        Season season = seasonCalculator.calculate(1);

        Assert.assertNotNull("season returned from calculator should not be null", season);

        Mockito.verify(seasonRepository, Mockito.times(1)).get(1);
        Mockito.verify(gameRepository, Mockito.times(1)).getBySeasonId(1);

        Assert.assertEquals("season id should be 1", 1, (int)season.getId());
        Assert.assertEquals("numGamesPlayed should be 0", 0, (int)season.getNumGamesPlayed());
        Assert.assertEquals("buyInCollected should be 0", 0, (int)season.getBuyInCollected());
        Assert.assertEquals("rebuyAddOnCollected should be 0", 0, (int)season.getRebuyAddOnCollected());
        Assert.assertEquals("tocCollected should be 0", 0, (int)season.getAnnualTocCollected());
        Assert.assertEquals("prizePot should be 0", 0, (int)season.getPrizePotCalculated());

        Assert.assertTrue("last calculated should be within the last few seconds", season.getLastCalculated().isAfter(LocalDateTime.now().minusSeconds(3)));

        Assert.assertEquals("payouts 0", 0, season.getPayouts().size());
        Assert.assertEquals("players 0", 0, season.getPlayers().size());
    }

    @Test
    public void test1Game() {

        Season currentSeason = Season.builder()
            .id(1)
            .build();
        Mockito.when(seasonRepository.get(1)).thenReturn(currentSeason);

        List<GamePlayer> gameSeasonPlayers = new ArrayList<>(10);
        for (int i = 0; i < 10; ++i) {
            GamePlayer gamePlayer = GamePlayer.builder()
                .id(i)
                .playerId(i)
                .gameId(1)
                .buyInCollected(GAME_DOUBLE_BUY_IN)
                .rebuyAddOnCollected(GAME_DOUBLE_REBUY)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .build();
            gameSeasonPlayers.add(gamePlayer);
        }

        List<GamePlayer> gameNonSeasonPlayers = new ArrayList<>(5);
        for (int i = 0; i < 5; ++i) {
            GamePlayer gamePlayer = GamePlayer.builder()
                .id(i)
                .playerId(i)
                .buyInCollected(GAME_DOUBLE_BUY_IN)
                .rebuyAddOnCollected(GAME_DOUBLE_REBUY)
                .gameId(1)
                .build();
            gameNonSeasonPlayers.add(gamePlayer);
        }

        List<GamePlayer> gameCombinedPlayers = new ArrayList<>(15);
        gameCombinedPlayers.addAll(gameSeasonPlayers);
        gameCombinedPlayers.addAll(gameNonSeasonPlayers);

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());
        Game currentGame = Game.builder()
            .doubleBuyIn(true)
            .build();
        Game calculatedGame = gameCalculator.calculate(currentGame, gameCombinedPlayers);
        List<Game> games = new LinkedList<>();
        games.add(calculatedGame);

        Mockito.when(gameRepository.getBySeasonId(1)).thenReturn(games);

        Season season = seasonCalculator.calculate(1);

        Assert.assertNotNull("season returned from calculator should not be null", season);

        Mockito.verify(seasonRepository, Mockito.times(1)).get(1);
        Mockito.verify(gameRepository, Mockito.times(1)).getBySeasonId(1);

        Assert.assertEquals("season id should be 1", 1, (int)season.getId());
        Assert.assertEquals("numGamesPlayed should be 1", 1, (int)season.getNumGamesPlayed());

        Assert.assertEquals("buyInCollected should be " + calculatedGame.getBuyInCollected(), (int)calculatedGame.getBuyInCollected(), (int)season.getBuyInCollected());
        Assert.assertEquals("rebuyAddOnCollected should be " + calculatedGame.getRebuyAddOnCollected(), (int)calculatedGame.getRebuyAddOnCollected(), (int)season.getRebuyAddOnCollected());
        Assert.assertEquals("tocCollected should be " + calculatedGame.getAnnualTocCollected(), (int)calculatedGame.getAnnualTocCollected(), (int)season.getAnnualTocCollected());
        Assert.assertEquals("prizePot should be " + calculatedGame.getPrizePotCalculated(), (int)calculatedGame.getPrizePotCalculated(), (int)season.getPrizePotCalculated());
//        Assert.assertEquals("prizePot should be 0", 0, (int)season.getPrizePot());
//
//        Assert.assertTrue("last calculated should be within the last few seconds", season.getLastCalculated().isAfter(LocalDateTime.now().minusSeconds(3)));
//
//        Assert.assertEquals("payouts 0", 0, season.getPayouts().size());
//        Assert.assertEquals("players 0", 0, season.getPlayers().size());
    }

}
