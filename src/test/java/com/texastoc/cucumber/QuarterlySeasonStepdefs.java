package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
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
public class QuarterlySeasonStepdefs extends SpringBootBaseIntegrationTest {

    private Season seasonToCreate;
    private Season seasonCreated;
    private HttpClientErrorException exception;

    @Given("^first quarterly season starts now$")
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

    @When("^the quarterly seasons are created$")
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

    @Then("^four quarterly seasons should be created$")
    public void the_start_date_should_be_now() throws Exception {
        Assert.assertTrue(seasonCreated.getQuarterlySeasons().size() == 4);

        for (int i = 0; i < 4; ++i) {
            QuarterlySeason qSeason = seasonCreated.getQuarterlySeasons().get(i);
            Assert.assertTrue(qSeason.getId() > 0);
            Assert.assertEquals((int)i + 1, (int)qSeason.getQuarter());

            Assert.assertEquals((int)seasonToCreate.getQuarterlyTocPerGame(), (int)qSeason.getTocPerGame());
            Assert.assertEquals((int)seasonToCreate.getQuarterlyNumPayouts(), (int)qSeason.getNumPayouts());

            Assert.assertTrue(qSeason.getTocCollected() == 0);

            LocalDate qSeasonExpectedEnd = LocalDate.now().plusWeeks(13 * (i + 1)).minusDays(1);

            Assert.assertEquals(seasonToCreate.getStart().plusWeeks(13 * (i)), qSeason.getStart());
            Assert.assertEquals(qSeasonExpectedEnd, qSeason.getEnd());

            Assert.assertTrue(qSeason.getNumGamesPlayed() == 0);
            Assert.assertTrue(qSeason.getNumGames() == 13 || qSeason.getNumGames() == 14);

            Assert.assertTrue(qSeason.getPlayers() == null || qSeason.getPlayers().size() == 0);
            Assert.assertTrue(qSeason.getPayouts() == null || qSeason.getPayouts().size() == 0);
        }

    }

//    @Then("^response is \"([^\"]*)\"$")
//    public void response_is(String expected) throws Exception {
//        Assert.assertEquals(expected, exception.getStatusCode().toString());
//    }

}
