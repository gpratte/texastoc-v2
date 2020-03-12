package com.texastoc.service;

import com.texastoc.model.user.Player;
import com.texastoc.repository.PlayerRepository;
import com.texastoc.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final RoleRepository roleRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public PlayerService(PlayerRepository playerRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.playerRepository = playerRepository;
    this.roleRepository = roleRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Transactional
  public Player create(Player player) {
    Player playerToCreate = Player.builder()
        .firstName(player.getFirstName())
        .lastName(player.getLastName())
        .email(player.getEmail())
        .phone(player.getPhone())
        .password(player.getPassword() == null ? null : bCryptPasswordEncoder.encode(player.getPassword()))
        .build();

    int id = playerRepository.save(playerToCreate);

    // Default to USER role
    roleRepository.save(id);

    player.setId(id);
    return player;
  }

  @Transactional
  public void update(Player player) {
    Player playerToUpdate = playerRepository.get(player.getId());
    playerToUpdate.setFirstName(player.getFirstName());
    playerToUpdate.setLastName(player.getLastName());
    playerToUpdate.setEmail(player.getEmail());
    playerToUpdate.setPhone(player.getPhone());

    if (player.getPassword() != null) {
      playerToUpdate.setPassword(bCryptPasswordEncoder.encode(player.getPassword()));
    }

    playerRepository.update(playerToUpdate);
  }

  @Transactional(readOnly = true)
  public Player get(int id) {
    Player player = playerRepository.get(id);
    player.setPassword(null);
    return player;
  }
}
