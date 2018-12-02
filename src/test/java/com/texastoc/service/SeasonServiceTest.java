package com.texastoc.service;

import com.texastoc.model.game.Game;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.GameRepository;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import com.texastoc.testutil.SeasonTestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeasonServiceTest {

    private SeasonService service;

    @MockBean
    private SeasonRepository seasonRepository;

    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;

    @MockBean
    private GameRepository gameRepository;

    @Before
    public void before() {
        service = new SeasonService(seasonRepository, qSeasonRepository, gameRepository);
    }

    @Test
    public void testCreateSeason() {

        // Arrange
        Season expected = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(10)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();

        Mockito.when(seasonRepository.save((Season) notNull())).thenReturn(1);
        Mockito.when(qSeasonRepository.save((QuarterlySeason) notNull())).thenReturn(1);

        // Act
        Season actual = service.createSeason(expected);

        // Arrange a bit more. End date should the day before the start date next year
        expected.setEnd(LocalDate.now().plusYears(1).minusDays(1));

        // Assert
        SeasonTestUtil.assertCreated(expected, actual);
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
            .quarter(i)
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

        Mockito.when(seasonRepository.get( ArgumentMatchers.eq(1) ))
            .thenReturn(Season.builder()
                .id(1)
                .build());

        Mockito.when(qSeasonRepository.getBySeasonId( ArgumentMatchers.eq(1) )).thenReturn(qSeasons);

        Mockito.when(gameRepository.getBySeasonId( ArgumentMatchers.eq(1) )).thenReturn(games);

        // Act
        Season actualSeason = service.getSeason(1);

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
