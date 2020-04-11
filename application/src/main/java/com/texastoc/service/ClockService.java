package com.texastoc.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClockService {

  private final SimpMessagingTemplate template;

  public ClockService(SimpMessagingTemplate template) {
    this.template = template;
  }

  //@Scheduled(fixedDelay = 3000)
  public void sendClock() {
    template.convertAndSend("/topic/greetings", "" + System.currentTimeMillis());
  }

}
