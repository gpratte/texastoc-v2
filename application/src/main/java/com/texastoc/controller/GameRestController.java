package com.texastoc.controller;

import com.texastoc.controller.request.CreateGamePlayerRequest;
import com.texastoc.controller.request.CreateGameRequest;
import com.texastoc.controller.request.UpdateGamePlayerRequest;
import com.texastoc.controller.request.UpdateGameRequest;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
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

    @PutMapping("/api/v2/games/players/{id}")
    public void updateGamePlayer(@PathVariable("id") int id, @RequestBody @Valid UpdateGamePlayerRequest ugpr) {
        GamePlayer gamePlayer = gameService.getGamePlayer(id);

        gamePlayer.setPlayerId(ugpr.getPlayerId());
        gamePlayer.setFinish(ugpr.getFinish());
        gamePlayer.setKnockedOut(ugpr.getKnockedOut());
        gamePlayer.setRoundUpdates(ugpr.getRoundUpdates());
        gamePlayer.setBuyInCollected(ugpr.getBuyInCollected());
        gamePlayer.setRebuyAddOnCollected(ugpr.getRebuyAddOnCollected());
        gamePlayer.setAnnualTocCollected(ugpr.getAnnualTocCollected());
        gamePlayer.setQuarterlyTocCollected(ugpr.getQuarterlyTocCollected());
        gamePlayer.setChop(ugpr.getChop());

        gameService.updateGamePlayer(gamePlayer);
    }

}
