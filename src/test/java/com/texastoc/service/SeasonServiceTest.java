package com.texastoc.service;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.repository.QuarterlySeasonRepository;
import com.texastoc.repository.SeasonRepository;
import com.texastoc.testutil.SeasonTestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.notNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeasonServiceTest {

    private SeasonService service;

    @MockBean
    private SeasonRepository seasonRepository;

    @MockBean
    private QuarterlySeasonRepository qSeasonRepository;

    @Before
    public void before() {
        service = new SeasonService(seasonRepository, qSeasonRepository);
    }

    @Test
    public void createSeason() {

        // Arrange
        Season expected = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(10)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();

        Mockito.when(seasonRepository.save( (Season)notNull() )).thenReturn(1);
        Mockito.when(qSeasonRepository.save( (QuarterlySeason)notNull() )).thenReturn(1);

        // Act
        Season actual = service.createSeason(expected);

        // Arrange a bit more. End date should the day before the start date next year
        expected.setEnd(LocalDate.now().plusYears(1).minusDays(1));

        // Assert
        SeasonTestUtil.assertCreated(expected, actual);
    }

}
