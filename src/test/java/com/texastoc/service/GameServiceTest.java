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
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameServiceTest {

    private GameService service;

    @MockBean
    private GameRepository gameRepository;

    @Before
    public void before() {
        service = new GameService(gameRepository);
    }

    @Test
    public void testCreateGame() {

        // Arrange
        Game expected = Game.builder()
            .date(LocalDate.now())
            .start(LocalDateTime.now())
            .build();

        Mockito.when(gameRepository.save((Game) notNull())).thenReturn(1);

        // Act
        Game actual = service.createGame(expected);

        // Assert
        Assert.assertNotNull("new game should not be null", actual);
    }

}