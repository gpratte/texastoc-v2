package com.texastoc.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.texastoc.TestConstants;
import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.controller.request.UpdateGameRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
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

        return restTemplate.postForObject(endpoint() + "/seasons", entity, Season.class);

    }

    protected Game createGame(CreateGameRequest createGameRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String createGameRequestAsJson = mapper.writeValueAsString(createGameRequest);
        HttpEntity<String> entity = new HttpEntity<>(createGameRequestAsJson ,headers);

        return restTemplate.postForObject(endpoint() + "/games", entity, Game.class);
    }

    protected void updateGame(int gameId, UpdateGameRequest updateGameRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String updateGameRequestAsJson = mapper.writeValueAsString(updateGameRequest);
        HttpEntity<String> entity = new HttpEntity<>(updateGameRequestAsJson ,headers);

        restTemplate.put(endpoint() + "/games/" + gameId, entity);
    }

    protected GamePlayer addPlayerToGame(CreateGamePlayerRequest cgpr) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String createGamePlayerRequestAsJson = mapper.writeValueAsString(cgpr);
        HttpEntity<String> entity = new HttpEntity<>(createGamePlayerRequestAsJson ,headers);

        return restTemplate.postForObject(endpoint() + "/games/players", entity, GamePlayer.class);
    }

    protected GamePlayer addFirstTimePlayerToGame(FirstTimeGamePlayer firstTimeGamePlayer) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String firstTimeGamePlayerRequestAsJson = mapper.writeValueAsString(firstTimeGamePlayer);
        HttpEntity<String> entity = new HttpEntity<>(firstTimeGamePlayerRequestAsJson ,headers);

        return restTemplate.postForObject(endpoint() + "/games/players/first", entity, GamePlayer.class);
    }

    protected void updatePlayerInGame(int gamePlayerId, UpdateGamePlayerRequest ugpr) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String updateGamePlayerRequestAsJson = mapper.writeValueAsString(ugpr);
        HttpEntity<String> entity = new HttpEntity<>(updateGamePlayerRequestAsJson ,headers);

        restTemplate.put(endpoint() + "/games/players/" + gamePlayerId, entity);
    }

    protected void deletePlayerFromGame(int gamePlayerId) throws JsonProcessingException {
        restTemplate.delete(endpoint() + "/games/players/" + gamePlayerId);
    }

    protected void finalizeGame(int gameId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.put(endpoint() + "/games/" + gameId + "/finalize", entity);
    }

}
