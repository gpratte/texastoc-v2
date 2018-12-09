package com.texastoc.testutil;

import com.texastoc.TestConstants;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import org.junit.Assert;

import java.time.LocalDate;

public class SeasonTestUtil implements TestConstants {

    public static void assertCreated(LocalDate start, Season actual) {
        Assert.assertTrue(actual.getId() > 0);
        Assert.assertEquals(KITTY_PER_GAME, (int)actual.getKittyPerGame());
        Assert.assertEquals(TOC_PER_GAME, (int)actual.getTocPerGame());
        Assert.assertEquals(QUARTERLY_TOC_PER_GAME, (int)actual.getQuarterlyTocPerGame());
        Assert.assertEquals(QUARTERLY_NUM_PAYOUTS, (int)actual.getQuarterlyNumPayouts());

        Assert.assertTrue(actual.getBuyInCollected() == 0);
        Assert.assertTrue(actual.getRebuyAddOnCollected() == 0);
        Assert.assertTrue(actual.getTocCollected() == 0);
        Assert.assertTrue(actual.getNumGamesPlayed() == 0);

        Assert.assertEquals(start, actual.getStart());

        Assert.assertEquals(actual.getStart().plusYears(1).minusDays(1), actual.getEnd());

        Assert.assertTrue(actual.getNumGames() == 52 || actual.getNumGames() == 53);

        Assert.assertTrue(actual.getPlayers() == null || actual.getPlayers().size() == 0);
        Assert.assertTrue(actual.getPayouts() == null || actual.getPayouts().size() == 0);

        Assert.assertEquals(4, actual.getQuarterlySeasons().size());

        for (int i = 0; i < 4; ++i) {
            QuarterlySeason qSeason = actual.getQuarterlySeasons().get(i);
            Assert.assertTrue(qSeason.getId() > 0);
            Assert.assertEquals((int) i + 1, (int) qSeason.getQuarter().getValue());

            Assert.assertEquals((int) QUARTERLY_TOC_PER_GAME, (int) qSeason.getTocPerGame());
            Assert.assertEquals((int) QUARTERLY_NUM_PAYOUTS, (int) qSeason.getNumPayouts());

            Assert.assertTrue(qSeason.getTocCollected() == 0);

            LocalDate qSeasonExpectedEnd = LocalDate.now().plusWeeks(13 * (i + 1)).minusDays(1);

            Assert.assertEquals(start.plusWeeks(13 * (i)), qSeason.getStart());
            Assert.assertEquals(qSeasonExpectedEnd, qSeason.getEnd());

            Assert.assertTrue(qSeason.getNumGamesPlayed() == 0);
            Assert.assertTrue(qSeason.getNumGames() == 13 || qSeason.getNumGames() == 14);

            Assert.assertTrue(qSeason.getPlayers() == null || qSeason.getPlayers().size() == 0);
            Assert.assertTrue(qSeason.getPayouts() == null || qSeason.getPayouts().size() == 0);

        }
    }
}
