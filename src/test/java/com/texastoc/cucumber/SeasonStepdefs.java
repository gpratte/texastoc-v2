package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.season.Season;
import com.texastoc.testutil.SeasonTestUtil;
import cucumber.api.PendingException;
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

@Ignore
public class SeasonStepdefs extends SpringBootBaseIntegrationTest {

    private Season seasonToCreate;
    private Season seasonCreated;
    private Season seasonRetrieved;
    private HttpClientErrorException exception;

    @Given("^season starts now$")
    public void season_starts_now() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(10)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season starts now all else minimal$")
    public void season_starts_now_all_else_minimal() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(0)
            .tocPerGame(0)
            .quarterlyTocPerGame(0)
            .quarterlyNumPayouts(1)
            .build();
    }

    @When("^the season is created$")
    public void the_season_is_created() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonToCreateAsJson = mapper.writeValueAsString(seasonToCreate);
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
        SeasonTestUtil.assertCreated(seasonToCreate, seasonCreated);
    }

    @Given("^season start date is missing$")
    public void season_start_date_is_missing() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .kittyPerGame(10)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season kitty per game is missing$")
    public void season_kitty_per_game_is_missing() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season kitty per game is negative$")
    public void season_kitty_per_game_is_negative() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(-1)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season toc per game is missing$")
    public void season_toc_per_game_is_missing() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season toc per game is negative$")
    public void season_toc_per_game_is_negative() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .tocPerGame(-10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season quarterly toc per game is missing$")
    public void season_quarterly_toc_per_game_is_missing() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .tocPerGame(2)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season quarterly toc per game is negative$")
    public void season_quarterly_toc_per_game_is_negative() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .tocPerGame(2)
            .quarterlyTocPerGame(-1)
            .quarterlyNumPayouts(3)
            .build();
    }

    @Given("^season quarterly num payouts is missing$")
    public void season_quarterly_num_payouts_is_missing() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .tocPerGame(2)
            .quarterlyTocPerGame(3)
            .build();
    }

    @Given("^season quarterly num payouts is zero$")
    public void season_quarterly_num_payouts_is_zero() throws Exception {
        // Arrange
        seasonToCreate = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(1)
            .tocPerGame(2)
            .quarterlyTocPerGame(3)
            .quarterlyNumPayouts(0)
            .build();
    }

    @Then("^response is \"([^\"]*)\"$")
    public void response_is(String expected) throws Exception {
        Assert.assertEquals(expected, exception.getStatusCode().toString());
    }

    @And("^the season is retrieved$")
    public void the_season_is_retrieved() throws Exception {
        System.out.println("!!! before calling endpoint " + seasonCreated.getId());
        seasonRetrieved = restTemplate.getForObject(endpoint() + "/seasons/" + seasonCreated.getId(), Season.class);
    }

    @Then("^the season should have four quaters$")
    public void the_season_should_have_four_quaters() throws Exception {
        Assert.assertNotNull("season retrieved should not be null", seasonRetrieved);
        Assert.assertNotNull("season retrieved quarterly seasons should not be null", seasonRetrieved.getQuarterlySeasons());
        Assert.assertEquals(4, seasonRetrieved.getQuarterlySeasons().size());
    }

}
