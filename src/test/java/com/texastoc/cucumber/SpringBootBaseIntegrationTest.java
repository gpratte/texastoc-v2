package com.texastoc.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.model.season.Season;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringBootBaseIntegrationTest {

    private final String SERVER_URL = "http://localhost";
    private String V2_ENDPOINT;

    static final int KITTY_PER_GAME = 9;
    static final int TOC_PER_GAME = 8;
    static final int QUARTERLY_TOC_PER_GAME = 7;
    static final int QUARTERLY_NUM_PAYOUTS = 3;
    static final int BRIAN_BAKER_PLAYER_ID = 1;
    static final String BRIAN_BAKER_NAME = "Brian Baker";

    @LocalServerPort
    private int port;

    protected RestTemplate restTemplate;

    public SpringBootBaseIntegrationTest() {
        restTemplate = new RestTemplate();
    }

    protected String endpoint() {
        if (V2_ENDPOINT == null) {
            V2_ENDPOINT = SERVER_URL + ":" + port + "/api/v2";
        }
        return V2_ENDPOINT;
    }

    protected Season createSeason() throws Exception {
        return createSeason(LocalDate.now(), KITTY_PER_GAME, TOC_PER_GAME, QUARTERLY_TOC_PER_GAME, QUARTERLY_NUM_PAYOUTS);

    }

    protected Season createSeason(LocalDate start, int kittyPerGame, int tocPerGame, int quarterlyTocPerGame, int quarterlyNumPayouts) throws Exception {

        Season season = Season.builder()
            .start(LocalDate.now())
            .kittyPerGame(10)
            .tocPerGame(10)
            .quarterlyTocPerGame(10)
            .quarterlyNumPayouts(3)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonAsJson = mapper.writeValueAsString(season);
        HttpEntity<String> entity = new HttpEntity<>(seasonAsJson, headers);
        System.out.println(seasonAsJson);

        return restTemplate.postForObject(endpoint() + "/seasons", entity, Season.class);

    }

}
