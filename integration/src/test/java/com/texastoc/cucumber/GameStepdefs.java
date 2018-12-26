package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGameRequest;
import com.texastoc.model.game.Game;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

@Ignore
public class GameStepdefs extends SpringBootBaseIntegrationTest {

    private CreateGameRequest createGameRequest;
    private Game gameCreated;
    private Game gameRetrieved;
    private HttpClientErrorException exception;

    @Before
    public void before() {
        createGameRequest = null;
        gameCreated = null;
        gameRetrieved = null;
        exception = null;
    }


    @Given("^the game starts now$")
    public void the_game_starts_now() throws Exception {
        // Arrange
        createSeason();

        createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();
    }

    @Given("^the double buy in game starts now$")
    public void the_double_buy_in_game_starts_now() throws Exception {
        // Arrange
        createSeason();

        createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(true)
            .transportRequired(false)
            .build();
    }

    @Given("^the game supplies need to be moved$")
    public void the_game_supplies_need_to_be_moved() throws Exception {
        // Arrange
        createSeason();

        createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(true)
            .build();
    }

    @When("^the game is created$")
    public void the_game_is_created() throws Exception {
        gameCreated = createGame(createGameRequest);
    }

    @And("^the retrieved game is updated and retrieved$")
    public void the_retrieved_game_is_updated_and_retrieved() throws Exception {

        UpdateGameRequest updateGameRequest = UpdateGameRequest.builder()
            .hostId(gameRetrieved.getHostId())
            .date(gameRetrieved.getDate())
            .doubleBuyIn(true)
            .transportRequired(true)
            .payoutDelta(1)
            .build();
        updateGame(gameRetrieved.getId(), updateGameRequest);

        gameRetrieved = restTemplate.getForObject(endpoint() + "/games/" + gameCreated.getId(),Game.class);
    }

    @When("^the game is created and retrieved$")
    public void the_game_is_created_and_retrieved() throws Exception {
        gameCreated = createGame(createGameRequest);

        gameRetrieved = restTemplate.getForObject(endpoint() + "/games/" + gameCreated.getId(),Game.class);
    }

    @Then("^the game is normal$")
    public void the_game_is_normal() throws Exception {
        assertNewGame(gameCreated);
    }

    @Then("^the game is not double buy in nor transport required$")
    public void the_game_is_not_double_buy_in_nor_transport_required() throws Exception {
        Assert.assertFalse("double buy in should be false", gameCreated.getDoubleBuyIn());
        Assert.assertFalse("transport required should be false", gameCreated.getTransportRequired());
    }

    @Then("^the game is double buy-in, transport and delta changed$")
    public void the_game_is_double_buy_in_transport_and_delta_changed() throws Exception {
        Assert.assertTrue("double buy in should be true", gameRetrieved.getDoubleBuyIn());
        Assert.assertTrue("transport required should be true", gameRetrieved.getTransportRequired());
        Assert.assertEquals("payout delta should be 1", 1, (int)gameRetrieved.getPayoutDelta());
    }

    @Then("^the game is double buy in$")
    public void the_game_is_double_buy_in() throws Exception {
        Assert.assertNotNull("game create should not be null", gameCreated);

        // Game setup variables
        Assert.assertTrue("double buy in should be true", gameCreated.getDoubleBuyIn());
        Assert.assertFalse("transport required should be false", gameCreated.getTransportRequired());
        Assert.assertEquals("buy in cost should come from season", GAME_DOUBLE_BUY_IN, (int)gameCreated.getBuyInCost());
        Assert.assertEquals("re buy cost should come from season", GAME_DOUBLE_REBUY, (int)gameCreated.getRebuyAddOnCost());
        Assert.assertEquals("re buy toc debit cost should come from season", GAME_DOUBLE_REBUY_TOC_DEBIT, (int)gameCreated.getRebuyAddOnTocDebit());
    }

    @Then("^the game transport supplies flag is set$")
    public void the_game_transport_supplies_flag_is_set() throws Exception {
        Assert.assertNotNull("game create should not be null", gameCreated);

        // Game setup variables
        Assert.assertFalse("double buy in should be false", gameCreated.getDoubleBuyIn());
        Assert.assertTrue("transport required should be true", gameCreated.getTransportRequired());
    }

    @Then("^the retrieved game is normal$")
    public void the_retrieved_game_is_normal() throws Exception {
        assertNewGame(gameRetrieved);
    }

    @Then("^the retrieved game has no players$")
    public void the_retrieved_game_has_no_players() throws Exception {
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be zero", 0, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players should be zero", 0, (int)gameRetrieved.getPlayers().size());
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be zero", 0, (int)gameRetrieved.getPayouts().size());
    }

    private void assertNewGame(Game game) throws Exception {
        Assert.assertNotNull("game created should not be null", game);
        Assert.assertTrue("game id should be greater than 0", game.getId() > 0);
        Assert.assertTrue("game season id should be greater than 0", game.getSeasonId() > 0);
        Assert.assertTrue("game quarterly season id should be greater than 0", game.getQSeasonId() > 0);
        Assert.assertEquals("game quarter should be 1", 1, game.getQuarter().getValue());

        Assert.assertEquals("game host id should be " + BRIAN_BAKER_PLAYER_ID, BRIAN_BAKER_PLAYER_ID, (int)game.getHostId());
        Assert.assertEquals("game host name should be " + BRIAN_BAKER_NAME, BRIAN_BAKER_NAME, game.getHostName());

        // Game setup variables
        Assert.assertEquals("kitty cost should come from season", KITTY_PER_GAME, (int)game.getKittyCost());
        Assert.assertEquals("buy in cost should come from season", GAME_BUY_IN, (int)game.getBuyInCost());
        Assert.assertEquals("re buy cost should come from season", GAME_REBUY, (int)game.getRebuyAddOnCost());
        Assert.assertEquals("re buy toc debit cost should come from season", GAME_REBUY_TOC_DEBIT, (int)game.getRebuyAddOnTocDebit());
        Assert.assertEquals("toc cost should come from season", TOC_PER_GAME, (int)game.getAnnualTocCost());
        Assert.assertEquals("quarterly toc cost should come from season", QUARTERLY_TOC_PER_GAME, (int)game.getQuarterlyTocCost());

        // Game time variables
        Assert.assertEquals("game kitty collected should be zero", 0, (int)game.getKittyCollected());
        Assert.assertEquals("game buy in should be zero", 0, (int)game.getBuyInCollected());
        Assert.assertEquals("game rebuy should be zero", 0, (int)game.getRebuyAddOnCollected());
        Assert.assertEquals("game annual toc collected should be zero", 0, (int)game.getAnnualTocCollected());
        Assert.assertEquals("game quarterly toc collected should be zero", 0, (int)game.getQuarterlyTocCollected());
        Assert.assertFalse("not finalized", game.getFinalized());
        Assert.assertNull("started should be null", game.getStarted());

    }

}
