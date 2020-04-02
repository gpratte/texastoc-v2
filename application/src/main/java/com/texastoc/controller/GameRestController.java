package com.texastoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.texastoc.controller.request.*;
import com.texastoc.exception.NotFoundException;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.game.Seating;
import com.texastoc.service.GameService;
import com.texastoc.service.SeatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

  @GetMapping("/api/v2/games/current")
  public Game getCurrentGame() {
    Game game = gameService.getCurrentGame();
    if (game != null) {
      return game;
    }
    throw new NotFoundException("Current game not found");
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
    return gameService.createGamePlayer(cgpr);
  }

  // TODO api/v1/games/{id}/players/first
  @PostMapping("/api/v2/games/players/first")
  public GamePlayer createGamePlayer(@RequestBody @Valid FirstTimeGamePlayer firstTimeGamePlayer) {
    return gameService.createFirstTimeGamePlayer(firstTimeGamePlayer);
  }

  // TODO api/v1/games/{id}/players/{pid}
  @PutMapping("/api/v2/games/players/{id}")
  public GamePlayer updateGamePlayer(@PathVariable("id") int id, @RequestBody @Valid UpdateGamePlayerRequest ugpr) {
    return gameService.updateGamePlayer(ugpr);
  }

  // TODO api/v1/games/{id}/players/{pid}
  @DeleteMapping("/api/v2/games/players/{id}")
  public void deleteGamePlayer(@PathVariable("id") int id) {
    gameService.deleteGamePlayer(id);
  }

  @PostMapping("/api/v2/games/seats")
  public Seating seat(@RequestBody SeatingRequest seatingRequest) throws JsonProcessingException {
    return seatingService.seat(seatingRequest.getGameId(), seatingRequest.getNumSeatsPerTable(), seatingRequest.getTableRequests());
  }

}
