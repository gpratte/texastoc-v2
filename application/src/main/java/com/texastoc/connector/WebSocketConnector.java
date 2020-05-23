package com.texastoc.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketConnector {

  private final SimpMessagingTemplate template;

  public WebSocketConnector(SimpMessagingTemplate template) {
    this.template = template;
  }

  /**
   * Send a message on the websocket
   */
  @Scheduled(fixedDelay = 3000)
  public void sendClock() {
    template.convertAndSend("/topic/greetings", "" + System.currentTimeMillis());
  }


}
