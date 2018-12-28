package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
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
public class GamePlayersStepdefs extends SpringBootBaseIntegrationTest {

    private Integer gameId;
    private Integer numPlayers;
    private Game gameRetrieved;
    private List<GamePlayer> gamePlayers = new LinkedList<>();
    private List<UpdateGamePlayerRequest> gamePlayersUpdated = new LinkedList<>();
    private List<FirstTimeGamePlayer> firstTimeGamePlayers = new LinkedList<>();

    private Random random = new Random(System.currentTimeMillis());

    @Before
    public void before() {
        gameId = null;
        numPlayers = null;
        gameRetrieved = null;
        gamePlayers.clear();
        gamePlayersUpdated.clear();
        firstTimeGamePlayers.clear();
    }


    @When("^a game is created$")
    public void a_game_is_created() throws Exception {
        // Arrange
        createSeason();

        CreateGameRequest createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

        gameId = createGame(createGameRequest).getId();
    }


    @And("^a player is added without buy-in$")
    public void a_player_is_added_without_buy_in() throws Exception {

        CreateGamePlayerRequest createGamePlayerRequest = CreateGamePlayerRequest.builder()
            .playerId(BRIAN_BAKER_PLAYER_ID)
            .gameId(gameId)
            .build();

        gamePlayers.add(addPlayerToGame(createGamePlayerRequest));
    }

    @And("^a player is added with buy-in$")
    public void a_player_is_added_with_buy_in() throws Exception {

        CreateGamePlayerRequest createGamePlayerRequest =CreateGamePlayerRequest.builder()
            .playerId(BRIAN_BAKER_PLAYER_ID)
            .gameId(gameId)
            .buyInCollected(GAME_BUY_IN)
            .build();

        gamePlayers.add(addPlayerToGame(createGamePlayerRequest));
    }

    @And("^two players are added with buy-in$")
    public void two_players_are_added_with_buy_in() throws Exception {

        CreateGamePlayerRequest createGamePlayerRequest = CreateGamePlayerRequest.builder()
            .playerId(BRIAN_BAKER_PLAYER_ID)
            .gameId(gameId)
            .buyInCollected(GAME_BUY_IN)
            .build();
        gamePlayers.add(addPlayerToGame(createGamePlayerRequest));

        createGamePlayerRequest = CreateGamePlayerRequest.builder()
            .playerId(ANDY_THOMAS_PLAYER_ID)
            .gameId(gameId)
            .buyInCollected(GAME_BUY_IN)
            .build();
        gamePlayers.add(addPlayerToGame(createGamePlayerRequest));
    }

    @And("^the game is retrieved$")
    public void the_game_is_retrieved() throws Exception {
        gameRetrieved = restTemplate.getForObject(endpoint() + "/games/" + gameId, Game.class);
    }

    @And("^the player is updated$")
    public void the_player_is_updated() throws Exception {

        UpdateGamePlayerRequest updateGamePlayerRequest = UpdateGamePlayerRequest.builder()
            .playerId(ANDY_THOMAS_PLAYER_ID)
            .finish(10)
            .knockedOut(true)
            .roundUpdates(true)
            .buyInCollected(GAME_BUY_IN)
            .rebuyAddOnCollected(GAME_REBUY)
            .annualTocCollected(TOC_PER_GAME)
            .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
            .build();

        GamePlayer gamePlayer = gamePlayers.get(0);
        updatePlayerInGame(gamePlayer.getId(), updateGamePlayerRequest);
        gamePlayersUpdated.add(updateGamePlayerRequest);
    }

    @And("^the player is deleted$")
    public void the_player_is_deleted() throws Exception {

        GamePlayer gamePlayer = gamePlayers.get(0);
        deletePlayerFromGame(gamePlayer.getId());
    }

    @And("^a first time player is added$")
    public void a_first_time_player_is_added() throws Exception {

        FirstTimeGamePlayer firstTimeGamePlayer = FirstTimeGamePlayer.builder()
            .firstName("Joe")
            .lastName("Schmoe")
            .email("joe.schmoe@texastoc.com")
            .gameId(gameId)
            .buyInCollected(GAME_BUY_IN)
            .annualTocCollected(TOC_PER_GAME)
            .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
            .build();

        firstTimeGamePlayers.add(firstTimeGamePlayer);
        gamePlayers.add(addFirstTimePlayerToGame(firstTimeGamePlayer));
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
        Assert.assertEquals("num of game payouts should be 0", 0, (int)gameRetrieved.getPayouts().size());
        Assert.assertEquals("kitty should be " + KITTY_PER_GAME, KITTY_PER_GAME, (int)gameRetrieved.getKittyCalculated());
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

    @Then("^the retrieved game has two players with buy-in$")
    public void the_retrieved_game_has_two_players_with_buy_in() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be 1", 1, (int)gameRetrieved.getPayouts().size());
        Assert.assertEquals("kitty should be " + KITTY_PER_GAME, KITTY_PER_GAME, (int)gameRetrieved.getKittyCalculated());
        Assert.assertNotNull("last calculated should be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 2", 2, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 2", 2, (int)gameRetrieved.getPlayers().size());

        for (int i = 0; i < gameRetrieved.getPlayers().size(); i++) {
            GamePlayer expected = gamePlayers.get(i);
            GamePlayer actual = gameRetrieved.getPlayers().get(i);

            Assert.assertNull("the game player points should be null", actual.getPoints());
            Assert.assertEquals("the game player buyInCollected should be " + GAME_BUY_IN, GAME_BUY_IN, (int)actual.getBuyInCollected());
            Assert.assertNull("the game player rebuyAddOnCollected should be null", actual.getRebuyAddOnCollected());
            Assert.assertNull("the game player annualTocCollected should be null", actual.getAnnualTocCollected());
            Assert.assertNull("the game player quarterlyTocCollected should be null", actual.getQuarterlyTocCollected());
            Assert.assertNull("the game player chop should be null", actual.getChop());
        }
    }

    @And("^random players are added$")
    public void random_players_are_added() throws Exception {

        numPlayers = 0;
        while (numPlayers < 3) {
            numPlayers = random.nextInt(50);
        }

        for (int i = 0; i < numPlayers; i++) {
            CreateGamePlayerRequest createGamePlayerRequest = CreateGamePlayerRequest.builder()
                .playerId(1)
                .gameId(gameId)
                .buyInCollected(GAME_BUY_IN)
                .annualTocCollected(random.nextBoolean() ? TOC_PER_GAME : null)
                .quarterlyTocCollected(random.nextBoolean() ? QUARTERLY_TOC_PER_GAME : null)
                .build();

            gamePlayers.add(addPlayerToGame(createGamePlayerRequest));
        }
    }


    @Then("^the retrieved game has random players$")
    public void the_retrieved_game_has_random_players() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertTrue("num of game payouts should be greater than 0", gameRetrieved.getPayouts().size() > 0);
        Assert.assertEquals("kitty should be " + KITTY_PER_GAME, KITTY_PER_GAME, (int)gameRetrieved.getKittyCalculated());
        Assert.assertNotNull("last calculated should be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be " + gamePlayers.size(), gamePlayers.size(), gamePlayers.size(), (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be " + gamePlayers.size(), gamePlayers.size(), (int)gameRetrieved.getPlayers().size());

        for (int i = 0; i < gameRetrieved.getPlayers().size(); i++) {
            GamePlayer expected = gamePlayers.get(i);
            GamePlayer actual = gameRetrieved.getPlayers().get(i);

            Assert.assertNull("the game player points should be null", actual.getPoints());
            Assert.assertEquals("the game player buyInCollected should be " + GAME_BUY_IN, GAME_BUY_IN, (int)actual.getBuyInCollected());

            if (expected.getRebuyAddOnCollected() == null) {
                Assert.assertNull("the game player rebuyAddOnCollected should be null", actual.getRebuyAddOnCollected());
            } else {
                Assert.assertEquals("the game player rebuyAddOnCollected should be " + expected.getRebuyAddOnCollected(), expected.getRebuyAddOnCollected(), actual.getRebuyAddOnCollected());
            }

            if (expected.getAnnualTocCollected() == null) {
                Assert.assertNull("the game player annualTocCollected should be null", actual.getAnnualTocCollected());
            } else {
                Assert.assertEquals("the game player annualTocCollected should be " + expected.getAnnualTocCollected(), expected.getAnnualTocCollected(), actual.getAnnualTocCollected());
            }

            if (expected.getQuarterlyTocCollected() == null) {
                Assert.assertNull("the game player quarterlyTocCollected should be null", actual.getQuarterlyTocCollected());
            } else {
                Assert.assertEquals("the game player quarterlyTocCollected should be " + expected.getQuarterlyTocCollected(), expected.getQuarterlyTocCollected(), actual.getQuarterlyTocCollected());
            }

            Assert.assertNull("the game player chop should be null", actual.getChop());
        }
    }

    @Then("^the retrieved game has one player with updates$")
    public void the_retrieved_game_has_one_player_with_updates() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be 0", 0, (int)gameRetrieved.getPayouts().size());
        Assert.assertEquals("kitty should be " + KITTY_PER_GAME, KITTY_PER_GAME, (int)gameRetrieved.getKittyCalculated());
        Assert.assertNotNull("last calculated should not be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 1", 1, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 1", 1, (int)gameRetrieved.getPlayers().size());

        UpdateGamePlayerRequest expected = gamePlayersUpdated.get(0);
        GamePlayer actual = gameRetrieved.getPlayers().get(0);

        Assert.assertNull("the game player points should be null", actual.getPoints());

        Assert.assertEquals("the game player finish should be " + expected.getFinish(), (int)expected.getFinish(), (int)actual.getFinish());
        Assert.assertEquals("the game player knockedOut should be " + expected.getKnockedOut(), expected.getKnockedOut(), actual.getKnockedOut());
        Assert.assertEquals("the game player roundUpdates should be " + expected.getRoundUpdates(), expected.getRoundUpdates(), actual.getRoundUpdates());
        Assert.assertEquals("the game player buyInCollected should be " + expected.getBuyInCollected(), (int)expected.getBuyInCollected(), (int)actual.getBuyInCollected());
        Assert.assertEquals("the game player rebuyAddOn should be " + expected.getRebuyAddOnCollected(), (int)expected.getRebuyAddOnCollected(), (int)actual.getRebuyAddOnCollected());
        Assert.assertEquals("the game player annualTocCollected should be " + expected.getAnnualTocCollected(), (int)expected.getAnnualTocCollected(), (int)actual.getAnnualTocCollected());
        Assert.assertEquals("the game player quarterlyTocCollected should be " + expected.getQuarterlyTocCollected(), (int)expected.getQuarterlyTocCollected(), (int)actual.getQuarterlyTocCollected());

        Assert.assertNull("the game player chop should be null", actual.getChop());
    }

    @Then("^the retrieved game does not have the player$")
    public void the_retrieved_game_does_not_have_the_player() throws Exception {

        // Assert game
        Assert.assertNotNull("game payouts should not be null", gameRetrieved.getPayouts());
        Assert.assertEquals("num of game payouts should be 0", 0, (int)gameRetrieved.getPayouts().size());
        Assert.assertNotNull("last calculated should not be null", gameRetrieved.getLastCalculated());

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 0", 0, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 0", 0, (int)gameRetrieved.getPlayers().size());
    }

    @Then("^the retrieved game has the first time player$")
    public void the_retrieved_game_has_the_first_time_player() throws Exception {

        // Assert game player
        Assert.assertNotNull("game players should not be null", gameRetrieved.getPlayers());
        Assert.assertEquals("num of game players should be 1", 1, (int)gameRetrieved.getNumPlayers());
        Assert.assertEquals("num of game players in list should be 1", 1, (int)gameRetrieved.getPlayers().size());

        FirstTimeGamePlayer expected = firstTimeGamePlayers.get(0);
        GamePlayer actual = gameRetrieved.getPlayers().get(0);
        Assert.assertEquals("game player created game id should be " + expected.getGameId(), expected.getGameId(), actual.getGameId());
        Assert.assertTrue("game player created player id should be set", actual.getPlayerId() > 0);
        Assert.assertEquals("game player created name should be " + expected.getFirstName() + " " + expected.getLastName(), (expected.getFirstName() + " " + expected.getLastName()), actual.getName());

        Assert.assertNull("the game player points should be null", actual.getPoints());
        Assert.assertEquals("the game player buyInCollected should be " + GAME_BUY_IN, GAME_BUY_IN, (int)actual.getBuyInCollected());
        Assert.assertNull("the game player rebuyAddOnCollected should be null", actual.getRebuyAddOnCollected());
        Assert.assertEquals("the game player annual toc should be " + TOC_PER_GAME, TOC_PER_GAME, (int)actual.getAnnualTocCollected());
        Assert.assertEquals("the game player quarterly toc should be " + QUARTERLY_TOC_PER_GAME, QUARTERLY_TOC_PER_GAME, (int)actual.getQuarterlyTocCollected());
        Assert.assertNull("the game player chop should be null", actual.getChop());
        Assert.assertNull("the game player finish should be null", actual.getFinish());
        Assert.assertNull("the game player knockedOut should be null", actual.getKnockedOut());
        Assert.assertNull("the game player roundUpdates should be null", actual.getRoundUpdates());
    }

}
