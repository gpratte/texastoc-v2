package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class GameAddPlayersStepdefs extends SpringBootBaseIntegrationTest {

    private Integer gameId;
    private Game gameRetrieved;
    private List<GamePlayer> gamePlayers = new ArrayList<>();

    @Before
    public void before() {
        gameId = null;
        gameRetrieved = null;
        gamePlayers.clear();
    }


    @When("^a game is created$")
    public void a_game_is_created() throws Exception {
        // Arrange
        createSeason();

        Game gameToCreate = Game.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

        gameId = createGame(gameToCreate).getId();
    }


    @And("^a player is added without buy-in$")
    public void a_player_is_added_without_buy_in() throws Exception {

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .playerId(BRIAN_BAKER_PLAYER_ID)
            .gameId(gameId)
            .name(BRIAN_BAKER_NAME)
            .build();

        gamePlayers.add(addPlayerToGame(gamePlayerToCreate));
    }

    @And("^a player is added with buy-in$")
    public void a_player_is_added_with_buy_in() throws Exception {

        GamePlayer gamePlayerToCreate = GamePlayer.builder()
            .playerId(BRIAN_BAKER_PLAYER_ID)
            .gameId(gameId)
            .name(BRIAN_BAKER_NAME)
            .buyInCollected(GAME_BUY_IN)
            .build();

        gamePlayers.add(addPlayerToGame(gamePlayerToCreate));
    }

    @And("^the game is retrieved$")
    public void the_game_is_retrieved() throws Exception {
        gameRetrieved = restTemplate.getForObject(endpoint() + "/games/" + gameId,Game.class);
    }

    @Then("^the retrieved game has one player no buy-in$")
    public void the_retrieved_game_has_one_player_no_buy_in() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be zero", 0, (int)gameRetrieved.getPayouts().size());
        Assert.assertNotNull("last calculated should be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 1", 1, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 1", 1, (int)gameRetrieved.getPlayers().size());

        GamePlayer expected = gamePlayers.get(0);
        GamePlayer actual = gameRetrieved.getPlayers().get(0);
        Assert.assertEquals("game player created game id should be " + expected.getGameId(), expected.getGameId(), actual.getGameId());
        Assert.assertEquals("game player created player id should be " + expected.getPlayerId(), expected.getPlayerId(), actual.getPlayerId());
        Assert.assertEquals("game player created name should be " + expected.getName(), expected.getName(), actual.getName());

        Assert.assertNull("the game player points should be null", actual.getPoints());
        Assert.assertNull("the game player buyInCollected should be null", actual.getBuyInCollected());
        Assert.assertNull("the game player rebuyAddOnCollected should be null", actual.getRebuyAddOnCollected());
        Assert.assertNull("the game player annualTocCollected should be null", actual.getAnnualTocCollected());
        Assert.assertNull("the game player quarterlyTocCollected should be null", actual.getQuarterlyTocCollected());
        Assert.assertNull("the game player chop should be null", actual.getChop());
        Assert.assertNull("the game player finish should be null", actual.getFinish());
        Assert.assertNull("the game player knockedOut should be null", actual.getKnockedOut());
        Assert.assertNull("the game player roundUpdates should be null", actual.getRoundUpdates());
    }

    @Then("^the retrieved game has one player with buy-in$")
    public void the_retrieved_game_has_one_player_with_buy_in() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be 1", 1, (int)gameRetrieved.getPayouts().size());
        Assert.assertNotNull("last calculated should be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 1", 1, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 1", 1, (int)gameRetrieved.getPlayers().size());

        GamePlayer expected = gamePlayers.get(0);
        GamePlayer actual = gameRetrieved.getPlayers().get(0);

        Assert.assertNull("the game player points should be null", actual.getPoints());
        Assert.assertEquals("the game player buyInCollected should be " + GAME_BUY_IN, GAME_BUY_IN, (int)actual.getBuyInCollected());
        Assert.assertNull("the game player rebuyAddOnCollected should be null", actual.getRebuyAddOnCollected());
        Assert.assertNull("the game player annualTocCollected should be null", actual.getAnnualTocCollected());
        Assert.assertNull("the game player quarterlyTocCollected should be null", actual.getQuarterlyTocCollected());
        Assert.assertNull("the game player chop should be null", actual.getChop());
    }

}
