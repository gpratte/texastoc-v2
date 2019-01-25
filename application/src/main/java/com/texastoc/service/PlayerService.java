package com.texastoc.service;

import com.texastoc.model.user.Player;
import com.texastoc.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player create(Player player) {
        int id = playerRepository.save(player);
        player.setId(id);
        return player;
    }

    @Transactional
    public void update(Player player) {
        playerRepository.update(player);
    }

    @Transactional(readOnly = true)
    public Player get(int id) {
        return playerRepository.get(id);
    }
}
