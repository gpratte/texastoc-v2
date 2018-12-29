package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Quarter;
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
    private Game gameRetrieved;

    @Before
    public void before() {
        gameId = null;
        gameRetrieved = null;
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
                .finish(i)
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

    @And("^the finalized game is retrieved$")
    public void the_finalized_game_is_retrieved() throws Exception {
        gameRetrieved = restTemplate.getForObject(endpoint() + "/games/" + gameId,Game.class);
    }

    @Then("^the retrieved game is properly calculated$")
    public void the_retrieved_game_is_properly_calculated() throws Exception {
        // Assert
        Assert.assertNotNull("new game should not be null", gameRetrieved);

        Assert.assertEquals("buy in collected should be ", GAME_BUY_IN * NUM_PLAYERS, gameRetrieved.getBuyInCollected());
        Assert.assertEquals("rebuy collected", GAME_REBUY * NUM_PLAYERS, gameRetrieved.getRebuyAddOnCollected());
        Assert.assertEquals("annual toc collected", TOC_PER_GAME * NUM_PLAYERS, gameRetrieved.getAnnualTocCollected());
        Assert.assertEquals("quarterly toc collected", QUARTERLY_TOC_PER_GAME * NUM_PLAYERS, gameRetrieved.getQuarterlyTocCollected());

        int totalCollected = GAME_BUY_IN * NUM_PLAYERS + GAME_REBUY * NUM_PLAYERS + TOC_PER_GAME * NUM_PLAYERS + QUARTERLY_TOC_PER_GAME * NUM_PLAYERS;
        Assert.assertEquals("total collected", totalCollected, gameRetrieved.getTotalCollected());


        Assert.assertEquals("annualTocFromRebuyAddOnCalculated", GAME_REBUY_TOC_DEBIT * NUM_PLAYERS, gameRetrieved.getAnnualTocFromRebuyAddOnCalculated());

        int rebuyLessToc = GAME_REBUY * NUM_PLAYERS - GAME_REBUY_TOC_DEBIT * NUM_PLAYERS;
        Assert.assertEquals("rebuyAddOnLessAnnualTocCalculated", rebuyLessToc, gameRetrieved.getRebuyAddOnLessAnnualTocCalculated());

        int totalToc = TOC_PER_GAME * NUM_PLAYERS + QUARTERLY_TOC_PER_GAME * NUM_PLAYERS + GAME_REBUY_TOC_DEBIT * NUM_PLAYERS;
        Assert.assertEquals("totalCombinedTocCalculated", totalToc, gameRetrieved.getTotalCombinedTocCalculated());

        int kitty = KITTY_PER_GAME;
        Assert.assertEquals("kitty calculated", kitty, gameRetrieved.getKittyCalculated());

        int prizePot = totalCollected - totalToc - kitty;
        Assert.assertEquals("prizePotCalculated", prizePot, gameRetrieved.getPrizePotCalculated());

        Assert.assertTrue("not finalized", gameRetrieved.isFinalized());

    }
}
