package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGameRequest;
import com.texastoc.model.game.Game;
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
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

@Ignore
public class SuppliesStepdefs extends SpringBootBaseIntegrationTest {

    Supply supplyToCreate;
    List<Supply> suppliesRetrieved;

    @Before
    public void before() {
        supplyToCreate = null;
        suppliesRetrieved = null;
    }

    @Given("^chairs have been bought$")
    public void chairs_have_been_bought() throws Exception {
        supplyToCreate = Supply.builder()
            .amount(11)
            .type(SupplyType.CHAIRS)
            .date(LocalDate.now())
            .build();
    }

    @When("^the supply is created$")
    public void the_supply_is_created() throws Exception {
        createSupply(supplyToCreate);
    }

    @Then("^the supplies are retrieved$")
    public void the_supplies_are_retrieved() throws Exception {
        ResponseEntity<List<Supply>> response = restTemplate.exchange(
            endpoint() + "/supplies",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Supply>>(){});
        suppliesRetrieved = response.getBody();
    }

    @Then("^then supply is in the list$")
    public void then_supply_is_in_the_list() throws Exception {
        Assert.assertNotNull("supply list not null", suppliesRetrieved);
        Assert.assertEquals("supply list 1", 1, suppliesRetrieved.size());

        Supply supplyRetrieved = suppliesRetrieved.get(0);
        Assert.assertEquals("amount", supplyToCreate.getAmount(), supplyRetrieved.getAmount());
        Assert.assertEquals("date", supplyToCreate.getDate(), supplyRetrieved.getDate());
        Assert.assertEquals("type", supplyToCreate.getType(), supplyRetrieved.getType());
        Assert.assertNull("description null", supplyRetrieved.getDescription());
    }

}
