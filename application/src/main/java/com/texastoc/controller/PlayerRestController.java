package com.texastoc.controller;

import com.texastoc.model.user.Player;
import com.texastoc.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SuppressWarnings("unused")
@RestController
public class PlayerRestController {

  private final PlayerService playerService;

  public PlayerRestController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @PostMapping("/api/v2/players")
  public Player createPlayer(@RequestBody Player player) {
    return playerService.create(player);
  }

  @PutMapping("/api/v2/players/{id}")
  public void updatePlayer(@PathVariable("id") int id, @RequestBody @Valid Player player) {
    player.setId(id);
    playerService.update(player);
  }

  @GetMapping("/api/v2/players/{id}")
  public Player getPlayer(@PathVariable("id") int id) {
    return playerService.get(id);
  }

}
