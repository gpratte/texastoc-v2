package com.texastoc.service.calculator;

import com.texastoc.TestConstants;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
public class QuarterlyCalculatorTest implements TestConstants {

    private QuarterlySeasonCalculator qSeasonCalculator;
    private GameCalculator gameCalculator;

    private Random random = new Random(System.currentTimeMillis());

    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private GamePlayerRepository gamePlayerRepository;
    @MockBean
    private ConfigRepository configRepository;
    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;

    @Before
    public void before() {
        qSeasonCalculator = new QuarterlySeasonCalculator(qSeasonRepository, gamePlayerRepository);
        gameCalculator = new GameCalculator(gameRepository, configRepository);
    }

    @Test
    public void testNoGames() {

        QuarterlySeason currentSeason = QuarterlySeason.builder()
            .id(1)
            .build();
        Mockito.when(qSeasonRepository.getById(1)).thenReturn(currentSeason);

        Mockito.when(gamePlayerRepository.selectQuarterlyTocPlayersByQuarterlySeasonId(1)).thenReturn(Collections.emptyList());

        QuarterlySeason qSeason = qSeasonCalculator.calculate(1);

        Mockito.verify(qSeasonRepository, Mockito.times(1)).getById(1);
        Mockito.verify(gamePlayerRepository, Mockito.times(1)).selectQuarterlyTocPlayersByQuarterlySeasonId(1);

        Assert.assertNotNull("quarterly season not null", qSeason);
        Assert.assertEquals("quarterly season id 1", 1, (int)qSeason.getId());

        Assert.assertEquals("quarter has no games played", 0, qSeason.getNumGamesPlayed());
        Assert.assertEquals("tocCollected is 0", 0, qSeason.getTocCollected());

        Assert.assertEquals("players 0", 0, qSeason.getPlayers().size());
        Assert.assertEquals("payouts 0", 0, qSeason.getPayouts().size());
    }

    @Test
    public void test1Games() {

        QuarterlySeason currentSeason = QuarterlySeason.builder()
            .id(1)
            .build();
        Mockito.when(qSeasonRepository.getById(1)).thenReturn(currentSeason);

        List<GamePlayer> gameQSeasonPlayers = new ArrayList<>(10);
        List<Integer> expectedPoints = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            int points = 0;
            if (i % 3 == 0 && i != 0) {
                points = i;
                expectedPoints.add(points);
            }

            GamePlayer gamePlayer = GamePlayer.builder()
                .id(i)
                .playerId(i)
                .gameId(1)
                .buyInCollected(GAME_DOUBLE_BUY_IN)
                .rebuyAddOnCollected(GAME_DOUBLE_REBUY)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .points(points)
                .build();
            gameQSeasonPlayers.add(gamePlayer);
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
        gameCombinedPlayers.addAll(gameQSeasonPlayers);
        gameCombinedPlayers.addAll(gameNonSeasonPlayers);

        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());
        Game currentGame = Game.builder()
            .doubleBuyIn(true)
            .build();
        Game calculatedGame = gameCalculator.calculate(currentGame, gameCombinedPlayers);
        List<Game> calculatedGames = new LinkedList<>();
        calculatedGames.add(calculatedGame);

        Mockito.when(gameRepository.getByQuarterlySeasonId(1)).thenReturn(calculatedGames);
        Mockito.when(gamePlayerRepository.selectQuarterlyTocPlayersByQuarterlySeasonId(1)).thenReturn(gameQSeasonPlayers);

        int buyInCollected = 0;
        int rebuyAddOnCollected = 0;
        int annualTocCollected = 0;
        int totalCollected = 0;
        int annualTocFromRebuyAddOnCalculated = 0;
        int rebuyAddOnLessAnnualTocCalculated = 0;
        int totalCombinedAnnualTocCalculated = 0;
        int kittyCalculated = 0;
        int prizePotCalculated = 0;

        for (Game game : calculatedGames) {
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

    }

}
