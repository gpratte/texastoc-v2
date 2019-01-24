package com.texastoc.controller;

import com.texastoc.model.supply.Supply;
import com.texastoc.model.user.Player;
import com.texastoc.service.PlayerService;
import com.texastoc.service.SupplyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
