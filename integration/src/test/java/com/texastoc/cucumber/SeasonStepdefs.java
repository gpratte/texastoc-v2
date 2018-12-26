package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.TestUtils;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.model.game.Game;
import com.texastoc.model.season.Season;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
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
import java.util.ArrayList;
import java.util.List;

@Ignore
public class SeasonStepdefs extends SpringBootBaseIntegrationTest {

    private LocalDate start;
    private Season seasonCreated;
    private Season seasonRetrieved;
    private List<Game> games = new ArrayList<>();
    private HttpClientErrorException exception;

    @Before
    public void before() {
        start = null;
        seasonCreated = null;
        seasonRetrieved = null;
        exception = null;
        games.clear();
    }

    @Given("^season starts now$")
    public void season_starts_now() throws Exception {
        // Arrange
        start = LocalDate.now();
    }

    @When("^the season is created$")
    public void the_season_is_created() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonToCreateAsJson = mapper.writeValueAsString(start);
        HttpEntity<String> entity = new HttpEntity<>(seasonToCreateAsJson ,headers);
        System.out.println(seasonToCreateAsJson);

        seasonCreated = restTemplate.postForObject(endpoint() + "/seasons", entity, Season.class);
    }

    @When("^attempting to create the season$")
    public void attempting_to_create_the_season() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonToCreateAsJson = mapper.writeValueAsString(start);
        HttpEntity<String> entity = new HttpEntity<>(seasonToCreateAsJson ,headers);
        System.out.println(seasonToCreateAsJson);

        try {
            seasonCreated = restTemplate.postForObject(endpoint() + "/seasons", entity, Season.class);
        } catch (HttpClientErrorException e) {
            exception = e;
        }
    }

    @Then("^the start date should be now$")
    public void the_start_date_should_be_now() throws Exception {
        TestUtils.assertCreatedSeason(start, seasonCreated);
    }

    @Given("^season start date is missing$")
    public void season_start_date_is_missing() throws Exception {
        // Arrange
        start = null;
    }

    @Then("^response is \"([^\"]*)\"$")
    public void response_is(String expected) throws Exception {
        Assert.assertEquals(expected, exception.getStatusCode().toString());
    }

    @And("^the season is retrieved$")
    public void the_season_is_retrieved() throws Exception {
        seasonRetrieved = restTemplate.getForObject(endpoint() + "/seasons/" + seasonCreated.getId(), Season.class);
    }

    @And("^a game is created for the season$")
    public void a_game_is_created_for_the_season() throws Exception {
        CreateGameRequest createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

        Game gameCreated = createGame(createGameRequest);
        games.add(gameCreated);
    }

    @Then("^the season should have four quarters$")
    public void the_season_should_have_four_quarters() throws Exception {
        Assert.assertNotNull("season retrieved should not be null", seasonRetrieved);
        Assert.assertNotNull("season retrieved quarterly seasons should not be null", seasonRetrieved.getQuarterlySeasons());
        Assert.assertEquals(4, seasonRetrieved.getQuarterlySeasons().size());
        Assert.assertNotNull("games should not be null", seasonRetrieved.getGames());
        Assert.assertEquals("season should have 0 games", 0, seasonRetrieved.getGames().size());
    }

    @Then("^the season should have one game and no players$")
    public void the_season_should_have_one_game_and_no_players() throws Exception {
        Assert.assertNotNull("season retrieved should not be null", seasonRetrieved);
        Assert.assertNotNull("season retrieved quarterly seasons should not be null", seasonRetrieved.getQuarterlySeasons());
        Assert.assertNotNull("games should not be null", seasonRetrieved.getGames());
        Assert.assertEquals("season should have 1 game", 1, seasonRetrieved.getGames().size());
    }

}
