package com.texastoc.service;

import com.texastoc.config.RoundsConfig;
import com.texastoc.model.game.clock.Clock;
import com.texastoc.model.game.clock.Round;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ClockService {

  private final SimpMessagingTemplate template;
  private final Map<Integer, Clock> clocks = new HashMap<>();
  private final RoundsConfig roundsConfig;

  public ClockService(SimpMessagingTemplate template, RoundsConfig roundsConfig) {
    this.template = template;
    this.roundsConfig = roundsConfig;
  }

  public Clock get(int gameId) {
    return getClock(gameId);
  }

  public void resume(int gameId) {
  }

  public void pause(int gameId) {
  }

  private Clock getClock(int gameId) {
    Clock clock = clocks.get(gameId);
    if (clock == null) {
      Round round1 = roundsConfig.getRounds().get(0);
      clock = Clock.builder()
        .gameId(gameId)
        .minutes(round1.getDuration())
        .seconds(0)
        .playing(false)
        .thisRound(round1)
        .nextRound(roundsConfig.getRounds().get(1))
        .build();
      clocks.put(gameId, clock);
    }
    return clock;
  }

  /**
   * Send a message on the websocket
   */
  //@Scheduled(fixedDelay = 3000)
  public void sendClock() {
    template.convertAndSend("/topic/greetings", "" + System.currentTimeMillis());
  }

}
