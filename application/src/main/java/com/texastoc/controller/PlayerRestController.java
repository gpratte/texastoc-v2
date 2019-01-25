package com.texastoc.controller;

import com.texastoc.model.user.Player;
import com.texastoc.service.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public void updatePlayer(@PathVariable("id") int id, @RequestBody @Valid Player updatePlayer) {
        Player player = playerService.get(id);
        player.setFirstName(updatePlayer.getFirstName());
        player.setLastName(updatePlayer.getLastName());
        player.setEmail(updatePlayer.getEmail());
        player.setPhone(updatePlayer.getPhone());
        player.setPassword(updatePlayer.getPassword());

        playerService.update(player);
    }

    @GetMapping("/api/v2/players/{id}")
    public Player getPlayer(@PathVariable("id") int id) {
        return playerService.get(id);
    }

}
