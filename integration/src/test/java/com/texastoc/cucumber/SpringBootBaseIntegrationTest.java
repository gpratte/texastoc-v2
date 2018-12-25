package com.texastoc.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.TestConstants;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.season.Season;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringBootBaseIntegrationTest implements TestConstants {

    private final String SERVER_URL = "http://localhost";
    private String V2_ENDPOINT;

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
        return createSeason(LocalDate.now());

    }

    protected Season createSeason(LocalDate start) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String seasonAsJson = mapper.writeValueAsString(start);
        HttpEntity<String> entity = new HttpEntity<>(seasonAsJson, headers);
        System.out.println(seasonAsJson);

        return restTemplate.postForObject(endpoint() + "/seasons", entity, Season.class);

    }

    protected Game createGame(Game game) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String gameToCreateAsJson = mapper.writeValueAsString(game);
        HttpEntity<String> entity = new HttpEntity<>(gameToCreateAsJson ,headers);
        System.out.println(gameToCreateAsJson);

        return restTemplate.postForObject(endpoint() + "/games", entity, Game.class);
    }

    protected GamePlayer addPlayerToGame(GamePlayer gamePlayer) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String gamePlayerToCreateAsJson = mapper.writeValueAsString(gamePlayer);
        HttpEntity<String> entity = new HttpEntity<>(gamePlayerToCreateAsJson ,headers);
        System.out.println(gamePlayerToCreateAsJson);

        return restTemplate.postForObject(endpoint() + "/games/players", entity, GamePlayer.class);
    }

}
