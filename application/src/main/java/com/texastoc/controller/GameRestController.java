package com.texastoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.texastoc.controller.request.*;
import com.texastoc.exception.NotFoundException;
import com.texastoc.model.game.FirstTimeGamePlayer;
import com.texastoc.model.game.Game;
import com.texastoc.model.game.GamePlayer;
import com.texastoc.model.game.Seating;
import com.texastoc.service.ClockService;
import com.texastoc.service.GameService;
import com.texastoc.service.SeatingService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SuppressWarnings("unused")
@RestController
public class GameRestController {

  private final GameService gameService;
  private final SeatingService seatingService;
  private final ClockService clockService;

  public GameRestController(GameService gameService, SeatingService seatingService, ClockService clockService) {
    this.gameService = gameService;
    this.seatingService = seatingService;
    this.clockService = clockService;
  }

  @PostMapping(value = "/api/v2/games", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Game createGame(@RequestBody @Valid CreateGameRequest createGameRequest) {
    return gameService.createGame(Game.builder()
      .hostId(createGameRequest.getHostId())
      .date(createGameRequest.getDate())
      .doubleBuyIn(createGameRequest.getDoubleBuyIn())
      .transportRequired(createGameRequest.getTransportRequired())
      .build());
  }

  @PutMapping(value = "/api/v2/games/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
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

  @GetMapping(value = "/api/v2/games", consumes = "application/vnd.texastoc.current+json")
  public Game getCurrentGame() {
    Game game = gameService.getCurrentGame();
    if (game != null) {
      return game;
    }
    throw new NotFoundException("Current game not found");
  }

  @PutMapping(value = "/api/v2/games/{id}", consumes = "application/vnd.texastoc.finalize+json")
  public void finalizeGame(@PathVariable("id") int id) {
    clockService.endClock(id);
    gameService.endGame(id);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(value = "/api/v2/games/{id}", consumes = "application/vnd.texastoc.unfinalize+json")
  public void unfinalizeGame(@PathVariable("id") int id) {
    gameService.openGame(id);
  }


  @PostMapping(value = "/api/v2/games/{id}/players", consumes = MediaType.APPLICATION_JSON_VALUE)
  public GamePlayer createGamePlayer(@PathVariable("id") int id, @RequestBody @Valid CreateGamePlayerRequest cgpr) {
    cgpr.setGameId(id);
    return gameService.createGamePlayer(cgpr);
  }

  @PostMapping(value = "/api/v2/games/{id}/players", consumes = "application/vnd.texastoc.new-player+json")
  public GamePlayer createGamePlayer(@PathVariable("id") int id, @RequestBody @Valid FirstTimeGamePlayer firstTimeGamePlayer) {
    firstTimeGamePlayer.setGameId(id);
    return gameService.createFirstTimeGamePlayer(firstTimeGamePlayer);
  }

  @PutMapping(value = "/api/v2/games/{id}/players/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public GamePlayer updateGamePlayer(@PathVariable("id") int id, @PathVariable("playerId") int playerId, @RequestBody @Valid UpdateGamePlayerRequest ugpr) {
    ugpr.setGameId(id);
    ugpr.setGamePlayerId(playerId);
    return gameService.updateGamePlayer(ugpr);
  }

  @PutMapping(value = "/api/v2/games/{id}/players/{playerId}", consumes = "application/vnd.texastoc.knockout+json")
  public GamePlayer toggleKnockedOut(@PathVariable("id") int id, @PathVariable("playerId") int playerId) {
    return gameService.toogleGamePlayerKnockedOut(id, playerId);
  }

  @DeleteMapping("/api/v2/games/{id}/players/{playerId}")
  public void deleteGamePlayer(@PathVariable("id") int id, @PathVariable("playerId") int playerId) {
    // TODO use the game id when deleting
    gameService.deleteGamePlayer(playerId);
  }

  @PostMapping(value = "/api/v2/games/{id}/seats", consumes = "application/vnd.texastoc.assign-seats+json")
  public Seating seats(@PathVariable("id") int id, @RequestBody SeatingRequest seatingRequest) throws JsonProcessingException {
    seatingRequest.setGameId(id);
    return seatingService.seat(seatingRequest.getGameId(), seatingRequest.getNumSeatsPerTable(), seatingRequest.getTableRequests());
  }

  @PostMapping(value = "/api/v2/games/{id}/seats", consumes = "application/vnd.texastoc.notify-seats+json")
  public void notifySeating(@PathVariable("id") int id) throws JsonProcessingException {
    gameService.notifySeating(id);
  }

}
