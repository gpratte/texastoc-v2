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
  private final Map<Integer, RunClock> threads = new HashMap<>();
  private final RoundsConfig roundsConfig;

  public ClockService(SimpMessagingTemplate template, RoundsConfig roundsConfig) {
    this.template = template;
    this.roundsConfig = roundsConfig;
  }

  public Clock get(int gameId) {
    Clock clock = getClock(gameId);
    int secondsRemaining = (int) (clock.getMillisRemaining() / 1000);
    int minutesRemaining = secondsRemaining / 60;
    secondsRemaining = secondsRemaining - minutesRemaining * 60;
    clock.setMinutes(minutesRemaining);
    clock.setSeconds(secondsRemaining);
    return clock;
  }

  public void resume(int gameId) {
    Clock clock = getClock(gameId);
    clock.setPlaying(true);

    if (!threads.containsKey(gameId)) {
      RunClock runClock = new RunClock(clock);
      new Thread(runClock).start();
      threads.put(gameId, runClock);
    }
  }

  public void pause(int gameId) {
    Clock clock = getClock(gameId);
    clock.setPlaying(false);
  }

  public void back(int gameId) {
    Clock clock = getClock(gameId);
    if (clock.getMillisRemaining() == 0) {
      // Move to previous round
      Round thisRound = findPreviousRound(clock.getThisRound());
      clock.setThisRound(thisRound);
      clock.setNextRound(findNextRound(thisRound));
      clock.setMillisRemaining(thisRound.getDuration() * 60 * 1000);
      return;
    }

    long millisRemaining = clock.getMillisRemaining() - 60000;
    if (millisRemaining < 0) {
      millisRemaining = 0;
    }
    clock.setMillisRemaining(millisRemaining);
  }

  public void forward(int gameId) {
    Clock clock = getClock(gameId);
    // If already at max time go to next round
    if (clock.getMillisRemaining() == (clock.getThisRound().getDuration() * 60 * 1000)) {
      // Move to next round
      Round thisRound = clock.getNextRound();
      clock.setThisRound(thisRound);
      clock.setNextRound(findNextRound(clock.getThisRound()));
      clock.setMillisRemaining(thisRound.getDuration() * 60 * 1000);
      return;
    }

    // add a minute
    long millisRemaining = clock.getMillisRemaining() + 60000;

    // Make sure not over maximum time
    if (millisRemaining > (clock.getThisRound().getDuration() * 60 * 1000)) {
      millisRemaining = clock.getThisRound().getDuration() * 60 * 1000;
    }
    clock.setMillisRemaining(millisRemaining);
  }

  public void endClock(int gameId) {
    RunClock runClock = threads.get(gameId);
    if (runClock != null) {
      runClock.endClock();
      threads.remove(gameId);
    }
    Clock clock = clocks.get(gameId);
    if (clock != null) {
      clock.setPlaying(false);
    }
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
        .millisRemaining(round1.getDuration() * 60 * 1000)
        .build();
      clocks.put(gameId, clock);
    }
    return clock;
  }

  private Round findPreviousRound(Round round) {
    for (int i = roundsConfig.getRounds().size() - 1; i >= 0; i--) {
      if (i == 0) {
        // return first round
        return roundsConfig.getRounds().get(0);
      }
      if (round.getName().equals(roundsConfig.getRounds().get(i).getName())) {
        return roundsConfig.getRounds().get(i - 1);
      }
    }
    // should never get here
    return null;
  }

  private Round findNextRound(Round round) {
    for (int i = 0; i < roundsConfig.getRounds().size(); i++) {
      if (i == roundsConfig.getRounds().size() - 1) {
        // last round repeats
        return roundsConfig.getRounds().get(roundsConfig.getRounds().size() - 1);
      }
      if (round.getName().equals(roundsConfig.getRounds().get(i).getName())) {
        return roundsConfig.getRounds().get(i + 1);
      }
    }
    // should never get here
    return null;
  }

  /**
   * Send a message on the websocket
   */
  //@Scheduled(fixedDelay = 3000)
  public void sendClock() {
    template.convertAndSend("/topic/greetings", "" + System.currentTimeMillis());
  }

  class RunClock implements Runnable {
    private Clock clock;
    private boolean end = false;

    public RunClock(Clock clock) {
      this.clock = clock;
    }

    public void endClock() {
      end = true;
    }

    @Override
    public void run() {
      while (!end) {
        if (clock.isPlaying()) {
          if (clock.getMillisRemaining() > 0) {
            // current round is running
            long start = System.currentTimeMillis();
            try {
              Thread.sleep(500l);
            } catch (InterruptedException e) {
              // Do nothing
            }
            long clockRan = System.currentTimeMillis() - start;
            System.out.print(". ");
            clock.setMillisRemaining(clock.getMillisRemaining() - clockRan);
          } else {
            // Move to next round
            Round thisRound = clock.getNextRound();
            clock.setThisRound(thisRound);
            clock.setNextRound(findNextRound(clock.getThisRound()));
            clock.setMillisRemaining(thisRound.getDuration() * 60 * 1000);
          }
        } else {
          // Sleep while not playing
          // TODO use thread notify instead
          try {
            System.out.print(".. ");
            Thread.sleep(1000l);
          } catch (InterruptedException e) {
            // Do nothing
          }
        }
      }
    }
  }
}
