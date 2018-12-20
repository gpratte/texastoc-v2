package com.texastoc.service;

import com.texastoc.TestConstants;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
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

import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
public class GameServiceTest implements TestConstants {

    private GameService service;

    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private SeasonRepository seasonRepository;
    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;

    @Before
    public void before() {
        service = new GameService(gameRepository, playerRepository, seasonRepository, qSeasonRepository);
    }

    @Test
    public void testCreateGame() {

        // Arrange
        LocalDate start = LocalDate.now();
        Game expected = Game.builder()
            .date(start)
            .hostId(1)
            .transportRequired(true)
            .doubleBuyIn(true)
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
        Game actual = service.createGame(expected);

        // Game repository called once
        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(Game.class));

        // Game argument match
        ArgumentCaptor<Game> gameArg = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository).save(gameArg.capture());
        Assert.assertEquals(start, gameArg.getValue().getDate());
        Assert.assertEquals(1, (int)gameArg.getValue().getHostId());
        Assert.assertTrue(gameArg.getValue().getTransportRequired());
        Assert.assertTrue(gameArg.getValue().getDoubleBuyIn());


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

}