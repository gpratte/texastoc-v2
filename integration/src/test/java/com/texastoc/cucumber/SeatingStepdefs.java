package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.Table;
import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
import cucumber.api.java.Before;
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

    @Before
    public void before() {
        gameId = null;
        tables = null;
    }

    @Given("^a game has players$")
    public void a_game_has_players() throws Exception {
        createSeason(LocalDate.now());
        Game game = createGame(CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build());

        gameId = game.getId();

        for (int i = 0; i <12 ; i++) {
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

    @When("^seating is done with 3 dead stacks$")
    public void seating_is_done_with_3_dead_stacks() throws Exception {
    }

    @Then("^all 15 seats have been assigned$")
    public void all_15_seats_have_been_assigned() throws Exception {
    }

}
