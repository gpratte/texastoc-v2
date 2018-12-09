package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

@Ignore
public class GameStepdefs extends SpringBootBaseIntegrationTest {

    private Game gameToCreate;
    private Game gameCreated;
    private HttpClientErrorException exception;

    @Given("^the game starts now$")
    public void the_game_starts_now() throws Exception {
        // Arrange
        createSeason();

        gameToCreate = Game.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

    }

    @When("^the game is created$")
    public void the_game_is_created() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonToCreateAsJson = mapper.writeValueAsString(gameToCreate);
        HttpEntity<String> entity = new HttpEntity<>(seasonToCreateAsJson ,headers);
        System.out.println(seasonToCreateAsJson);

        gameCreated = restTemplate.postForObject(endpoint() + "/games", entity, Game.class);
    }

    @Then("^the game belongs to the first quarter$")
    public void the_game_belongs_to_the_first_quarter() throws Exception {
        Assert.assertNotNull("game create should not be null", gameCreated);
        Assert.assertTrue("game season id should be greater than 0", gameCreated.getSeasonId() > 0);
        Assert.assertTrue("game quarterly season id should be greater than 0", gameCreated.getQSeasonId() > 0);
        Assert.assertEquals("game quarter should be 1", 1, gameCreated.getQuarter().getValue());

        Assert.assertEquals("game host id should be " + BRIAN_BAKER_PLAYER_ID, BRIAN_BAKER_PLAYER_ID, (int)gameCreated.getHostId());
        Assert.assertEquals("game host name should be " + BRIAN_BAKER_NAME, BRIAN_BAKER_NAME, gameCreated.getHostName());

        Assert.assertEquals("game number players should be zero", 0, (int)gameCreated.getNumPlayers());
        Assert.assertEquals("game buy in should be zero", 0, (int)gameCreated.getBuyInCollected());
        Assert.assertEquals("game rebuy should be zero", 0, (int)gameCreated.getRebuyAddOnCollected());
        Assert.assertEquals("game annual toc collected should be zero", 0, (int)gameCreated.getAnnualTocCollected());
        Assert.assertEquals("game quarterly toc collected should be zero", 0, (int)gameCreated.getQuarterlyTocCollected());
    }

}
