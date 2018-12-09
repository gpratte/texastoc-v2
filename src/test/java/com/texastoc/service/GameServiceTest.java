package com.texastoc.service;

import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.user.Player;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import com.texastoc.testutil.SeasonTestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
public class GameServiceTest {

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
        Game expected = Game.builder()
            .date(LocalDate.now())
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
                .kittyPerGame(10)
                .tocPerGame(9)
                .quarterlyTocPerGame(8)
                .build());

        // Act
        Game actual = service.createGame(expected);

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
        Assert.assertEquals("Supplies need transporting", expected.getTransportRequired(), actual.getTransportRequired());
        Assert.assertEquals("Buy in cost should be amount set for season which is 10", 10, (int)actual.getBuyInCost());
        Assert.assertEquals("Kitty cost should be amount set for season which is 10", 10, (int)actual.getKittyCost());


        Assert.assertEquals("Annual TOC be amount set for season which is 9", 9, (int)actual.getAnnualTocCost());
        Assert.assertEquals("Quarterly TOC be amount set for season which is 8", 8, (int)actual.getQuarterlyTocCost());


        Assert.assertNull("not started", actual.getStarted());


        Assert.assertEquals("No players", 0, (int)actual.getNumPlayers());
        Assert.assertEquals("No buy in", 0, (int)actual.getBuyInCollected());
        Assert.assertEquals("No re buy in", 0, (int)actual.getRebuyAddOnCollected());

    }

}