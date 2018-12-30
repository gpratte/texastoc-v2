package com.texastoc.service;

import com.texastoc.TestConstants;
import com.texastoc.TestUtils;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.ConfigRepository;
import com.texastoc.repository.GamePayoutRepository;
import com.texastoc.repository.GamePlayerRepository;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonPayoutRepository;
import com.texastoc.repository.QuarterlySeasonPlayerRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonPayoutRepository;
import com.texastoc.repository.SeasonPlayerRepository;
import com.texastoc.repository.SeasonRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
public class SeasonServiceTest implements TestConstants {

    private SeasonService service;

    @MockBean
    private SeasonRepository seasonRepository;
    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private ConfigRepository configRepository;
    @MockBean
    private GamePlayerRepository gamePlayerRepository;
    @MockBean
    private GamePayoutRepository gamePayoutRepository;
    @MockBean
    private SeasonPlayerRepository seasonPlayerRepository;
    @MockBean
    private SeasonPayoutRepository seasonPayoutRepository;
    @MockBean
    private QuarterlySeasonPlayerRepository qSeasonPlayerRepository;
    @MockBean
    private QuarterlySeasonPayoutRepository qSeasonPayoutRepository;

    @Before
    public void before() {
        service = new SeasonService(seasonRepository, qSeasonRepository, gameRepository, configRepository, gamePlayerRepository, gamePayoutRepository, seasonPlayerRepository, seasonPayoutRepository, qSeasonPlayerRepository, qSeasonPayoutRepository);
    }

    @Test
    public void testCreateSeason() {

        // Arrange
        LocalDate start = LocalDate.now();

        Mockito.when(seasonRepository.save((Season) notNull())).thenReturn(1);
        Mockito.when(qSeasonRepository.save((QuarterlySeason) notNull())).thenReturn(1);
        Mockito.when(configRepository.get()).thenReturn(TestConstants.getTocConfig());

        // Act
        Season actual = service.createSeason(start);

        // Assert
        TestUtils.assertCreatedSeason(start, actual);

        // Season repository called once
        Mockito.verify(seasonRepository, Mockito.times(1)).save(Mockito.any(Season.class));

        // Season argument has same start time
        ArgumentCaptor<Season> seasonArg = ArgumentCaptor.forClass(Season.class);
        Mockito.verify(seasonRepository).save(seasonArg.capture());
        Assert.assertEquals(start, seasonArg.getValue().getStart());

        // Config repository called one times
        Mockito.verify(configRepository, Mockito.times(1)).get();

        // Quarterly season repository called four times
        Mockito.verify(qSeasonRepository, Mockito.times(4)).save(Mockito.any(QuarterlySeason.class));

    }

    @Test
    public void testGetSeason() {

        // Arrange
        Season expectedSeason = Season.builder()
            // @formatter:off
            .id(1)
            .build();
            // @formatter:on

        List<QuarterlySeason> qSeasons = new ArrayList<>(4);
        for (int i = 1; i <= 4; i++) {
            // @formatter:off
            QuarterlySeason qSeason = QuarterlySeason.builder()
            .id(i)
            .quarter(Quarter.fromInt(i))
            .build();
            // @formatter:on
            qSeasons.add(qSeason);
        }

        List<Game> games = new LinkedList<>();
        // @formatter:off
        Game game = Game.builder()
            .id(1)
            .build();
        // @formatter:on
        games.add(game);
        expectedSeason.setGames(games);

        Mockito.when(seasonRepository.get(1))
            .thenReturn(Season.builder()
                .id(1)
                .build());

        Mockito.when(qSeasonRepository.getBySeasonId(1)).thenReturn(qSeasons);

        Mockito.when(gameRepository.getBySeasonId(1)).thenReturn(games);

        // Act
        Season actualSeason = service.getSeason(1);

        // Season repository called once
        Mockito.verify(seasonRepository, Mockito.times(1)).get(1);

        // QuarterlySeason repository called once
        Mockito.verify(qSeasonRepository, Mockito.times(1)).getBySeasonId(1);

        // Game repository called once
        Mockito.verify(gameRepository, Mockito.times(1)).getBySeasonId(1);


        // Assert
        Assert.assertNotNull("season return from get should not be null ", actualSeason);
        Assert.assertEquals(expectedSeason.getId(), actualSeason.getId());

        Assert.assertNotNull("quarterly seasons should not be null ", actualSeason.getQuarterlySeasons());
        Assert.assertEquals(4, actualSeason.getQuarterlySeasons().size());
        for (int i = 1; i <= 4; i++) {
            QuarterlySeason qSeason = actualSeason.getQuarterlySeasons().get(i-1);
            Assert.assertNotNull(qSeason);
            Assert.assertTrue(qSeason.getId() > 0);
        }

        Assert.assertNotNull("season games should not be null ", actualSeason.getGames());
        Assert.assertEquals(1, actualSeason.getGames().size());
        Assert.assertTrue(actualSeason.getGames().get(0).getId() > 0);

    }
}
