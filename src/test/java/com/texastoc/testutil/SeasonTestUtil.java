package com.texastoc.testutil;

import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import org.junit.Assert;

import java.time.LocalDate;

public class SeasonTestUtil {

    public static void assertCreated(Season expected, Season actual) {
        Assert.assertTrue(actual.getId() > 0);
        Assert.assertEquals(expected.getKittyPerGame(), actual.getKittyPerGame());
        Assert.assertEquals(expected.getTocPerGame(), actual.getTocPerGame());
        Assert.assertEquals(expected.getQuarterlyTocPerGame(), actual.getQuarterlyTocPerGame());
        Assert.assertEquals(expected.getQuarterlyNumPayouts(), actual.getQuarterlyNumPayouts());

        Assert.assertTrue(actual.getBuyInCollected() == 0);
        Assert.assertTrue(actual.getRebuyAddOnCollected() == 0);
        Assert.assertTrue(actual.getTocCollected() == 0);
        Assert.assertTrue(actual.getNumGamesPlayed() == 0);

        Assert.assertEquals(expected.getStart(), actual.getStart());

        Assert.assertEquals(actual.getStart().plusYears(1).minusDays(1), actual.getEnd());

        Assert.assertTrue(actual.getNumGames() == 52 || actual.getNumGames() == 53);

        Assert.assertTrue(actual.getPlayers() == null || actual.getPlayers().size() == 0);
        Assert.assertTrue(actual.getPayouts() == null || actual.getPayouts().size() == 0);

        Assert.assertEquals(4, actual.getQuarterlySeasons().size());

        for (int i = 0; i < 4; ++i) {
            QuarterlySeason qSeason = actual.getQuarterlySeasons().get(i);
            Assert.assertTrue(qSeason.getId() > 0);
            Assert.assertEquals((int) i + 1, (int) qSeason.getQuarter().getValue());

            Assert.assertEquals((int) expected.getQuarterlyTocPerGame(), (int) qSeason.getTocPerGame());
            Assert.assertEquals((int) expected.getQuarterlyNumPayouts(), (int) qSeason.getNumPayouts());

            Assert.assertTrue(qSeason.getTocCollected() == 0);

            LocalDate qSeasonExpectedEnd = LocalDate.now().plusWeeks(13 * (i + 1)).minusDays(1);

            Assert.assertEquals(expected.getStart().plusWeeks(13 * (i)), qSeason.getStart());
            Assert.assertEquals(qSeasonExpectedEnd, qSeason.getEnd());

            Assert.assertTrue(qSeason.getNumGamesPlayed() == 0);
            Assert.assertTrue(qSeason.getNumGames() == 13 || qSeason.getNumGames() == 14);

            Assert.assertTrue(qSeason.getPlayers() == null || qSeason.getPlayers().size() == 0);
            Assert.assertTrue(qSeason.getPayouts() == null || qSeason.getPayouts().size() == 0);

        }
    }
}
