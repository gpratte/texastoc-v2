package com.texastoc.cucumber;

import com.texastoc.model.supply.Supply;
import com.texastoc.model.supply.SupplyType;
import com.texastoc.model.user.Player;
import cucumber.api.PendingException;
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
public class PlayerStepdefs extends SpringBootBaseIntegrationTest {

    Player playerToCreate;
    Player playerCreated;

    @Before
    public void before() {
        playerToCreate = null;
    }

    @Given("^a new player$")
    public void a_new_player() throws Exception {
        playerToCreate = Player.builder()
            .firstName("John")
            .lastName("Luther")
            .build();

        playerCreated = createPlayer(playerToCreate);
    }

    @When("^the player password is updated$")
    public void the_player_password_is_updated() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^the player is retrieved$")
    public void the_player_is_retrieved() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the player has the expected encoded password$")
    public void the_player_has_the_expected_encoded_password() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

}
