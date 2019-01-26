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
import java.util.UUID;

@Ignore
public class PlayerStepdefs extends SpringBootBaseIntegrationTest {

    Player playerToCreate;
    Player playerCreated;
    Player updatePlayer;
    Player playerRetrieved;
    String token;

    @Before
    public void before() {
        playerToCreate = null;
        playerCreated = null;
        updatePlayer = null;
        playerRetrieved = null;
        token = null;
    }

    @Given("^a new player$")
    public void a_new_player() throws Exception {
        playerToCreate = Player.builder()
            .firstName("John")
            .lastName("Luther")
            .build();

        playerCreated = createPlayer(playerToCreate);
    }

    @Given("^a new player with email and password$")
    public void a_new_player_with_email_and_password() throws Exception {
        playerToCreate = Player.builder()
            .firstName("John")
            .lastName("Luther")
            .email("john.luther@example.com")
            .password("jacket")
            .build();

        playerCreated = createPlayer(playerToCreate);
    }
    @When("^the player password is updated$")
    public void the_player_password_is_updated() throws Exception {
        updatePlayer = Player.builder()
            .id(playerCreated.getId())
            .firstName(playerCreated.getFirstName())
            .lastName(playerCreated.getLastName())
            .email("abc@rst.com")
            .phone("2344322345")
            .password("password")
            .build();

        String token = login(ADMIN_EMAIL, ADMIN_PASSWORD);
        updatePlayer(updatePlayer, token);
    }

    @When("^the player is retrieved$")
    public void the_player_is_retrieved() throws Exception {
        String token = login("abc@rst.com", "password");
        playerRetrieved = getPlayer(playerCreated.getId(), token);
    }

    @When("^the player logs in$")
    public void the_player_logs_in() throws Exception {
        token = login(playerToCreate.getEmail(), playerCreated.getPassword());
    }

    @Then("^a token is returned$")
    public void a_token_is_returned() throws Exception {
        Assert.assertNotNull("token not null", token);
    }

    @Then("^the player has the expected encoded password$")
    public void the_player_has_the_expected_encoded_password() throws Exception {
        Assert.assertNotNull("player retrieved not null", playerRetrieved);
        Assert.assertEquals("id match", playerRetrieved.getId(), playerCreated.getId());
        Assert.assertEquals("first name match", updatePlayer.getFirstName(), playerRetrieved.getFirstName());
        Assert.assertEquals("last name match", updatePlayer.getLastName(), playerRetrieved.getLastName());
        Assert.assertEquals("email match", updatePlayer.getEmail(), playerRetrieved.getEmail());
        Assert.assertEquals("phone match", updatePlayer.getPhone(), playerRetrieved.getPhone());
    }

}
