package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.Seat;
import com.texastoc.model.game.Table;
import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@Ignore
public class SeatingStepdefs extends SpringBootBaseIntegrationTest {

    Integer gameId;
    List<Table> tables;
    Integer numPlayers;
    Integer numDeadStacks;
    Game game;

    @Before
    public void before() {
        gameId = null;
        tables = null;
        numPlayers = null;
        numDeadStacks = null;
        game = null;
    }

    @Given("^a game has 9 players$")
    public void a_game_has_9_players() throws Exception {
        createSeason(LocalDate.now());
        Game game = createGame(CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build());

        gameId = game.getId();

        numPlayers = 9;
        for (int i = 0; i < numPlayers; i++) {
            FirstTimeGamePlayer firstTimeGamePlayer = FirstTimeGamePlayer.builder()
                .firstName("Joe"+i)
                .lastName("Schmoe")
                .email("joe"+i+".schmoe@texastoc.com")
                .gameId(gameId)
                .buyInCollected(GAME_BUY_IN)
                .build();
            addFirstTimePlayerToGame(firstTimeGamePlayer);
        }
    }

    @Given("^a game has 11 players$")
    public void a_game_has_11_players() throws Exception {
        createSeason(LocalDate.now());
        Game game = createGame(CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build());

        gameId = game.getId();

        numPlayers = 11;
        for (int i = 0; i < numPlayers; i++) {
            FirstTimeGamePlayer firstTimeGamePlayer = FirstTimeGamePlayer.builder()
                .firstName("Joe"+i)
                .lastName("Schmoe")
                .email("joe"+i+".schmoe@texastoc.com")
                .gameId(gameId)
                .buyInCollected(GAME_BUY_IN)
                .build();
            addFirstTimePlayerToGame(firstTimeGamePlayer);
        }
    }

    @When("^seating is done with 0 dead stacks$")
    public void seating_is_done_with_0_dead_stacks() throws Exception {
        tables = seatPlayers(gameId, null, null);
    }

    @When("^seating is done with 2 dead stacks$")
    public void seating_is_done_with_2_dead_stacks() throws Exception {
        numDeadStacks = 2;
        tables = seatPlayers(gameId, numDeadStacks, null);
    }

    @Then("^9 seats have been assigned$")
    public void nine_seats_have_been_assigned() throws Exception {
        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("1 table", 1, tables.size());

        Table table = tables.get(0);
        Assert.assertEquals("table number should be 1", 1, table.getNumber());

        Assert.assertNotNull("seats should not be null", table.getSeats());

        List<Seat> seats = table.getSeats();
        Assert.assertEquals("9 seats", 9, seats.size());
    }

    @And("^the seated game is retrieved$")
    public void the_seated_game_is_retrieved() throws Exception {
        game = restTemplate.getForObject(endpoint() + "/games/" + gameId, Game.class);
    }

    @Then("^13 seats have been assigned$")
    public void thirteen_seats_have_been_assigned() throws Exception {
        Assert.assertNotNull("game should not be null", game);

        tables = game.getTables();
        Assert.assertNotNull("tables should not be null", tables);
        Assert.assertEquals("2 tables", 2, tables.size());

        Table table = tables.get(0);
        Assert.assertEquals("table number should be 1", 1, table.getNumber());
        Assert.assertNotNull("table 1 seats should not be null", table.getSeats());

        List<Seat> seats = table.getSeats();
        Assert.assertEquals("table 1 has 7 seats", 7, seats.size());

        table = tables.get(1);
        Assert.assertEquals("table number should be 2", 2, table.getNumber());
        Assert.assertNotNull("table 2 seats should not be null", table.getSeats());

        seats = table.getSeats();
        Assert.assertEquals("table 2 has 6 seats", 6, seats.size());

    }

}
