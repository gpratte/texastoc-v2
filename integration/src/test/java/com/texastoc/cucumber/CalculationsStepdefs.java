package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePayout;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Quarter;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import com.texastoc.model.season.SeasonPayout;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Ignore
public class CalculationsStepdefs extends SpringBootBaseIntegrationTest {

    private static final int NUM_PLAYERS = 10;

    private Integer gameId;
    private Season season;

    @Before
    public void before() {
        gameId = null;
        season = null;
    }


    @Given("^a game has 10 players all finished$")
    public void a_game_has_10_players_all_finished() throws Exception {
        // Arrange
        createSeason();

        CreateGameRequest createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

        gameId = createGame(createGameRequest).getId();

        List<GamePlayer> gamePlayers = new ArrayList<>(NUM_PLAYERS);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            FirstTimeGamePlayer firstTimeGamePlayer = FirstTimeGamePlayer.builder()
                .firstName("Joe" + i)
                .lastName("Schmoe")
                .email("joe" + i + "schmoe@texastoc.com")
                .gameId(gameId)
                .buyInCollected(GAME_BUY_IN)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .build();

            gamePlayers.add(addFirstTimePlayerToGame(firstTimeGamePlayer));
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            GamePlayer gamePlayer = gamePlayers.get(i);
            UpdateGamePlayerRequest ugpr = UpdateGamePlayerRequest.builder()
                .playerId(gamePlayer.getPlayerId())
                .finish(i+1)
                .buyInCollected(GAME_BUY_IN)
                .rebuyAddOnCollected(GAME_REBUY)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .build();

            updatePlayerInGame(gamePlayer.getId(), ugpr);
        }
    }

    @When("^the game is finalized$")
    public void the_game_is_finalized() throws Exception {
        finalizeGame(gameId);
    }

    @And("^the calculated season is retrieved$")
    public void the_calculated_season_is_retrieved() throws Exception {
        season = restTemplate.getForObject(endpoint() + "/seasons/current", Season.class);
        season = restTemplate.getForObject(endpoint() + "/seasons/" + season.getId(), Season.class);
    }

    @Then("^the game is properly calculated$")
    public void the_game_is_properly_calculated() throws Exception {
        // Assert
        Assert.assertNotNull("season should not be null", season);
        Assert.assertNotNull("games of the season should not be null", season.getGames());
        Assert.assertEquals("games size should be 1", 1, season.getGames().size());

        Game game = season.getGames().get(0);
        Assert.assertNotNull("game should not be null", game);

        checkGameRuntime(game);

        checkPayouts(game.getPrizePotCalculated(), game.getPayouts());

        checkPoints(game.getPlayers());
    }

    @Then("^the quarterly seasions are properly calculated$")
    public void the_quarterly_seasions_are_properly_calculated() {
        Assert.assertNotNull("season should not be null", season);
        Assert.assertNotNull("quarterly seasions of the season should not be null", season.getQuarterlySeasons());
        Assert.assertEquals("quarterly seasions size should be 4", 4, season.getQuarterlySeasons().size());

        QuarterlySeason qSeason = season.getQuarterlySeasons().get(0);
        Assert.assertNotNull("quarterly season not null", qSeason);
        Assert.assertEquals("quarter has 13 games", 13, qSeason.getNumGames());
        Assert.assertEquals("quarter has 1 game played", 1, qSeason.getNumGamesPlayed());
        Assert.assertEquals("qTocCollected is " + (QUARTERLY_TOC_PER_GAME * NUM_PLAYERS), QUARTERLY_TOC_PER_GAME * NUM_PLAYERS, qSeason.getQTocCollected());

//        Assert.assertEquals("players 10", 10, qSeason.getPlayers().size());
//        checkPoints(expectedPoints, qSeason.getPlayers());
//
//        List<SeasonPayout> payouts = qSeason.getPayouts();
//        Assert.assertEquals("payouts " + QUARTERLY_NUM_PAYOUTS, QUARTERLY_NUM_PAYOUTS, payouts.size());
//
//        int firstPlace = (int) Math.round(qTocCollected * 0.5d);
//        int secondPlace = (int) Math.round(qTocCollected * 0.3d);
//        int thirdPlace = qTocCollected - firstPlace - secondPlace;
//        int amounts[] = {firstPlace, secondPlace, thirdPlace};
//
//        for (int i = 0; i < 3; i++) {
//            int place = i+1;
//            boolean found = false;
//            for (SeasonPayout payout : payouts) {
//                if (payout.getPlace() == place) {
//                    found = true;
//                    Assert.assertEquals("payout " + place + " should be " + amounts[i], amounts[i], payout.getAmount());
//                }
//            }
//            Assert.assertTrue("should have found a payout for place " + place, found);
//        }

    }


    private void checkGameRuntime(Game game) {
        Assert.assertEquals("buy in collected should be ", GAME_BUY_IN * NUM_PLAYERS, game.getBuyInCollected());
        Assert.assertEquals("rebuy collected", GAME_REBUY * NUM_PLAYERS, game.getRebuyAddOnCollected());
        Assert.assertEquals("annual toc collected", TOC_PER_GAME * NUM_PLAYERS, game.getAnnualTocCollected());
        Assert.assertEquals("quarterly toc collected", QUARTERLY_TOC_PER_GAME * NUM_PLAYERS, game.getQuarterlyTocCollected());

        int totalCollected = GAME_BUY_IN * NUM_PLAYERS + GAME_REBUY * NUM_PLAYERS + TOC_PER_GAME * NUM_PLAYERS + QUARTERLY_TOC_PER_GAME * NUM_PLAYERS;
        Assert.assertEquals("total collected", totalCollected, game.getTotalCollected());


        Assert.assertEquals("annualTocFromRebuyAddOnCalculated", GAME_REBUY_TOC_DEBIT * NUM_PLAYERS, game.getAnnualTocFromRebuyAddOnCalculated());

        int rebuyLessToc = GAME_REBUY * NUM_PLAYERS - GAME_REBUY_TOC_DEBIT * NUM_PLAYERS;
        Assert.assertEquals("rebuyAddOnLessAnnualTocCalculated", rebuyLessToc, game.getRebuyAddOnLessAnnualTocCalculated());

        int totalToc = TOC_PER_GAME * NUM_PLAYERS + QUARTERLY_TOC_PER_GAME * NUM_PLAYERS + GAME_REBUY_TOC_DEBIT * NUM_PLAYERS;
        Assert.assertEquals("totalCombinedTocCalculated", totalToc, game.getTotalCombinedTocCalculated());

        int kitty = KITTY_PER_GAME;
        Assert.assertEquals("kitty calculated", kitty, game.getKittyCalculated());

        int prizePot = totalCollected - totalToc - kitty;
        Assert.assertEquals("prizePotCalculated", prizePot, game.getPrizePotCalculated());

        Assert.assertTrue("not finalized", game.isFinalized());
    }

    private void checkPayouts(int prizePot, List<GamePayout> gamePayouts) {

        Assert.assertNotNull("list of game payouts should not be null", gamePayouts);
        Assert.assertEquals("list of game payouts should be size 2", 2, gamePayouts.size());

        List<Integer> amounts = new ArrayList<>(2);
        int firstPlace = (int)Math.round(0.65 * prizePot);
        amounts.add(firstPlace);
        int secondPlace = (int)Math.round(0.35 * prizePot);
        amounts.add(secondPlace);

        double leftover = prizePot - firstPlace - secondPlace;
        leftover = Math.abs(leftover);

        int totalPaidOut = 0;

        for (int i = 0; i < gamePayouts.size(); ++i) {
            GamePayout gamePayout = gamePayouts.get(i);
            int amount = amounts.get(i);
            int place = i+1;

            Assert.assertEquals("payout should be place " + place, place, gamePayout.getPlace());
            Assert.assertEquals(amount, gamePayout.getAmount(), leftover);
            Assert.assertNull("payout chop amount should be null", gamePayout.getChopAmount());
            Assert.assertNull("payout chop percentage should be null", gamePayout.getChopPercent());
            totalPaidOut += gamePayout.getAmount();
        }

        Assert.assertEquals("sum of payouts for 10 players should be " + prizePot, prizePot, totalPaidOut);

    }

    private void checkPoints(List<GamePlayer> gamePlayers) {
        Assert.assertNotNull("list of game players should not be null", gamePlayers);
        Assert.assertEquals("list of game players should be 10", NUM_PLAYERS, gamePlayers.size());

        int pointsCount = 0;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPoints() != null) {
                ++pointsCount;
            }
        }

        int pointsChop = 0;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getChop() != null) {
                ++pointsChop;
            }
        }

        Assert.assertEquals("ten players should have points", NUM_PLAYERS, pointsCount);
        Assert.assertEquals("no players should have chop points", 0, pointsChop);

        int points[] = {70,	54,	42,	32,	25,	19,	15,	12,	9, 7};
        int expectedPoints = 0;
        for (int point : points) {
            expectedPoints += point;
        }

        int totalPoints = 0;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPoints() != null && gamePlayer.getPoints() > 0) {
                totalPoints += gamePlayer.getPoints();
            }
        }
        Assert.assertEquals("total points should be 285", expectedPoints, totalPoints);
    }
}
