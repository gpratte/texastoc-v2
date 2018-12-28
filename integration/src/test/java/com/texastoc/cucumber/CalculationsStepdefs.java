package com.texastoc.cucumber;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.junit.Ignore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Ignore
public class CalculationsStepdefs extends SpringBootBaseIntegrationTest {

    private static final int NUM_PLAYERS = 10;

    private Integer gameId;
    private Game gameRetrieved;

    @Before
    public void before() {
        gameId = null;
        gameRetrieved = null;
    }


    @When("^a game has 10 players all finished$")
    public void a_game_has_10_players_all_finished() throws Exception {
        // Arrange
        createSeason();

        CreateGameRequest createGameRequest = CreateGameRequest.builder()
            .date(LocalDate.now())
            .hostId(1)
            .doubleBuyIn(false)
            .transportRequired(false)
            .build();

        gameId = createGame(createGameRequest).getId();

        List<GamePlayer> gamePlayers = new ArrayList<>(NUM_PLAYERS);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            FirstTimeGamePlayer firstTimeGamePlayer = FirstTimeGamePlayer.builder()
                .firstName("Joe" + i)
                .lastName("Schmoe")
                .email("joe" + i + "schmoe@texastoc.com")
                .gameId(gameId)
                .buyInCollected(GAME_BUY_IN)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .build();

            gamePlayers.add(addFirstTimePlayerToGame(firstTimeGamePlayer));
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            GamePlayer gamePlayer = gamePlayers.get(i);
            UpdateGamePlayerRequest ugpr = UpdateGamePlayerRequest.builder()
                .playerId(gamePlayer.getPlayerId())
                .finish(i)
                .buyInCollected(GAME_BUY_IN)
                .rebuyAddOnCollected(GAME_REBUY)
                .annualTocCollected(TOC_PER_GAME)
                .quarterlyTocCollected(QUARTERLY_TOC_PER_GAME)
                .build();

            updatePlayerInGame(gamePlayer.getId(), ugpr);
        }
    }
}
