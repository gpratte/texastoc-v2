package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.season.QuarterlySeason;
import com.texastoc.model.season.Season;
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

    private LocalDate start;
    private Season seasonCreated;
    private Season seasonRetrieved;
    private HttpClientErrorException exception;


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
        System.out.println("!!!\n!!!\n!!!\n!!!\n!!!\n " + seasonToCreateAsJson);
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
        assertCreatedSeason(start, seasonCreated);
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
        System.out.println("!!! before calling endpoint " + seasonCreated.getId());
        seasonRetrieved = restTemplate.getForObject(endpoint() + "/seasons/" + seasonCreated.getId(), Season.class);
    }

    @Then("^the season should have four quarters$")
    public void the_season_should_have_four_quarters() throws Exception {
        Assert.assertNotNull("season retrieved should not be null", seasonRetrieved);
        Assert.assertNotNull("season retrieved quarterly seasons should not be null", seasonRetrieved.getQuarterlySeasons());
        Assert.assertEquals(4, seasonRetrieved.getQuarterlySeasons().size());
    }

    // TODO also have this in cucumber test, need to make DRY
    private void assertCreatedSeason(LocalDate start, Season actual) {
        Assert.assertTrue(actual.getId() > 0);

        Assert.assertEquals(start, actual.getStart());
        Assert.assertEquals(start.plusYears(1).minusDays(1), actual.getEnd());

        Assert.assertEquals(KITTY_PER_GAME, (int)actual.getKittyPerGame());
        Assert.assertEquals(TOC_PER_GAME, (int)actual.getTocPerGame());
        Assert.assertEquals(QUARTERLY_TOC_PER_GAME, (int)actual.getQuarterlyTocPerGame());
        Assert.assertEquals(QUARTERLY_NUM_PAYOUTS, (int)actual.getQuarterlyNumPayouts());
        Assert.assertEquals(GAME_BUY_IN, (int)actual.getBuyInCost());
        Assert.assertEquals(GAME_REBUY, (int)actual.getRebuyAddOnCost());
        Assert.assertEquals(GAME_REBUY_TOC_DEBIT, (int)actual.getRebuyAddOnTocDebit());
        Assert.assertEquals(GAME_DOUBLE_BUY_IN, (int)actual.getDoubleBuyInCost());
        Assert.assertEquals(GAME_DOUBLE_REBUY, (int)actual.getDoubleRebuyAddOnCost());
        Assert.assertEquals(GAME_DOUBLE_REBUY_TOC_DEBIT, (int)actual.getDoubleRebuyAddOnTocDebit());

        Assert.assertTrue(actual.getNumGames() == 52 || actual.getNumGames() == 53);
        Assert.assertTrue(actual.getNumGamesPlayed() == 0);
        Assert.assertTrue(actual.getBuyInCollected() == 0);
        Assert.assertTrue(actual.getRebuyAddOnCollected() == 0);
        Assert.assertTrue(actual.getTocCollected() == 0);

        Assert.assertEquals(false, actual.getFinalized());
        Assert.assertNull(actual.getLastCalculated());

        Assert.assertTrue(actual.getPlayers() == null || actual.getPlayers().size() == 0);
        Assert.assertTrue(actual.getPayouts() == null || actual.getPayouts().size() == 0);

        Assert.assertEquals(4, actual.getQuarterlySeasons().size());

        for (int i = 0; i < 4; ++i) {
            QuarterlySeason qSeason = actual.getQuarterlySeasons().get(i);
            Assert.assertTrue(qSeason.getId() > 0);
            Assert.assertEquals((int) i + 1, (int) qSeason.getQuarter().getValue());

            Assert.assertEquals((int) QUARTERLY_TOC_PER_GAME, (int) qSeason.getTocPerGame());
            Assert.assertEquals((int) QUARTERLY_NUM_PAYOUTS, (int) qSeason.getNumPayouts());

            Assert.assertTrue(qSeason.getTocCollected() == 0);

            LocalDate qSeasonExpectedEnd = LocalDate.now().plusWeeks(13 * (i + 1)).minusDays(1);

            Assert.assertEquals(start.plusWeeks(13 * (i)), qSeason.getStart());
            Assert.assertEquals(qSeasonExpectedEnd, qSeason.getEnd());

            Assert.assertTrue(qSeason.getNumGamesPlayed() == 0);
            Assert.assertTrue(qSeason.getNumGames() == 13 || qSeason.getNumGames() == 14);

            Assert.assertTrue(qSeason.getPlayers() == null || qSeason.getPlayers().size() == 0);
            Assert.assertTrue(qSeason.getPayouts() == null || qSeason.getPayouts().size() == 0);

        }
    }


}
