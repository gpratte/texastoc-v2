package com.texastoc.controller;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.SeatingRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.controller.request.UpdateGameRequest;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.game.Table;
import com.texastoc.service.GameService;
import com.texastoc.service.SeatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("unused")
@RestController
public class GameRestController {

    private final GameService gameService;
    private final SeatingService seatingService;

    public GameRestController(GameService gameService, SeatingService seatingService) {
        this.gameService = gameService;
        this.seatingService = seatingService;
    }

    @PostMapping("/api/v2/games")
    public Game createGame(@RequestBody @Valid CreateGameRequest createGameRequest) {
        return gameService.createGame(Game.builder()
            .hostId(createGameRequest.getHostId())
            .date(createGameRequest.getDate())
            .doubleBuyIn(createGameRequest.getDoubleBuyIn())
            .transportRequired(createGameRequest.getTransportRequired())
            .build());
    }

    @PutMapping("/api/v2/games/{id}")
    public void updateGame(@PathVariable("id") int id, @RequestBody @Valid UpdateGameRequest updateGameRequest) {
        Game game = gameService.getGame(id);
        game.setHostId(updateGameRequest.getHostId());
        game.setDate(updateGameRequest.getDate());
        game.setDoubleBuyIn(updateGameRequest.getDoubleBuyIn());
        game.setTransportRequired(updateGameRequest.getTransportRequired());
        game.setPayoutDelta(updateGameRequest.getPayoutDelta());

        gameService.updateGame(game);
    }

    @GetMapping("/api/v2/games/{id}")
    public Game getGame(@PathVariable("id") int id) {
        return gameService.getGame(id);
    }

    @PutMapping("/api/v2/games/{id}/finalize")
    public void finalizeGame(@PathVariable("id") int id) {
        gameService.endGame(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/v2/games/{id}/unfinalize")
    public void unfinalizeGame(@PathVariable("id") int id) {
        gameService.openGame(id);
    }


    // TODO api/v1/games/{id}/players
    @PostMapping("/api/v2/games/players")
    public GamePlayer createGamePlayer(@RequestBody @Valid CreateGamePlayerRequest cgpr) {
        GamePlayer gamePlayer = GamePlayer.builder()
            .playerId(cgpr.getPlayerId())
            .gameId(cgpr.getGameId())
            .buyInCollected(cgpr.getBuyInCollected())
            .annualTocCollected(cgpr.getAnnualTocCollected())
            .quarterlyTocCollected(cgpr.getQuarterlyTocCollected())
            .build();
        return gameService.createGamePlayer(gamePlayer);
    }

    // TODO api/v1/games/{id}/players/first
    @PostMapping("/api/v2/games/players/first")
    public GamePlayer createGamePlayer(@RequestBody @Valid FirstTimeGamePlayer firstTimeGamePlayer) {
        return gameService.createFirstTimeGamePlayer(firstTimeGamePlayer);
    }

    // TODO api/v1/games/{id}/players/{pid}
    @PutMapping("/api/v2/games/players/{id}")
    public void updateGamePlayer(@PathVariable("id") int id, @RequestBody @Valid UpdateGamePlayerRequest ugpr) {
        GamePlayer gamePlayer = gameService.getGamePlayer(id);

        gamePlayer.setPlayerId(ugpr.getPlayerId());
        gamePlayer.setPlace(ugpr.getPlace());
        gamePlayer.setKnockedOut(ugpr.getKnockedOut());
        gamePlayer.setRoundUpdates(ugpr.getRoundUpdates());
        gamePlayer.setBuyInCollected(ugpr.getBuyInCollected());
        gamePlayer.setRebuyAddOnCollected(ugpr.getRebuyAddOnCollected());
        gamePlayer.setAnnualTocCollected(ugpr.getAnnualTocCollected());
        gamePlayer.setQuarterlyTocCollected(ugpr.getQuarterlyTocCollected());
        gamePlayer.setChop(ugpr.getChop());

        gameService.updateGamePlayer(gamePlayer);
    }

    // TODO api/v1/games/{id}/players/{pid}
    @DeleteMapping("/api/v2/games/players/{id}")
    public void deleteGamePlayer(@PathVariable("id") int id) {
        gameService.deleteGamePlayer(id);
    }

    @PostMapping("/api/v2/games/seats")
    public List<Table> seat(@RequestBody @Valid SeatingRequest seatingRequest) {
        int gameId = seatingRequest.getGameId();
        return seatingService.seat(seatingRequest.getGameId(), seatingRequest.getNumDeadStacks(), seatingRequest.getTableRequests());
    }

}
