package com.texastoc.service;

import com.texastoc.model.user.Player;
import com.texastoc.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player create(Player player) {
        int id = playerRepository.create(player);
        player.setId(id);
        return player;
    }

    public void update(Player player) {
        playerRepository.update(player);
    }

    public Player get(int id) {
        return playerRepository.get(id);
    }
}
