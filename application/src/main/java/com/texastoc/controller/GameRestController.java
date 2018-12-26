package com.texastoc.controller;

import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public Game createGame(@RequestBody @Valid Game game) {
        return gameService.createGame(game);
    }

    @GetMapping("/api/v2/games/{id}")
    public Game getGame(@PathVariable("id") int id) {
        return gameService.getGame(id);
    }

    @PostMapping("/api/v2/games/players")
    public GamePlayer createGamePlayer(@RequestBody @Valid GamePlayer gamePlayer) {
        return gameService.createGamePlayer(gamePlayer);
    }

}
