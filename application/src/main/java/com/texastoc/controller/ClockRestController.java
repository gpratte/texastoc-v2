package com.texastoc.controller;

import com.texastoc.model.game.clock.Clock;
import com.texastoc.service.ClockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClockRestController {

  private final ClockService clockService;

  public ClockRestController(ClockService clockService) {
    this.clockService = clockService;
  }

  @GetMapping("/api/v2/games/{id}/clock")
  public Clock getClock(@PathVariable("id") int id) {
    return clockService.get(id);
  }

  @PostMapping(value = "/api/v2/games/{id}/clock", consumes = "application/vnd.texastoc.clock-resume+json")
  public void resume(@PathVariable("id") int id) {
    clockService.resume(id);
  }

  @PostMapping(value = "/api/v2/games/{id}/clock", consumes = "application/vnd.texastoc.clock-pause+json")
  public void pause(@PathVariable("id") int id) {
    clockService.pause(id);
  }
}
