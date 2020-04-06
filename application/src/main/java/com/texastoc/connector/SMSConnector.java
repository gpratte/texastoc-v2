package com.texastoc.connector;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SMSConnector {

  private boolean initialzed;
  private String twilioPhone;

  public SMSConnector(@Value("${twilio.sid:#{null}}") String sid,
                      @Value("${twilio.token:#{null}}") String token,
                      @Value("${twilio.phone:#{null}}") String phone) {
    twilioPhone = "+1" + phone;
    try {
      Twilio.init(sid, token);
      initialzed = true;
    } catch (Exception e) {
      log.error("Could not initialize Twilio", e);
      initialzed = false;
    }
  }

  public void text(String phone, String body) {
    if (!initialzed) return;

    try {
      Message.creator(new PhoneNumber("+1" + phone), // to
        new PhoneNumber(twilioPhone), // from
        body).create();
    } catch (Exception e) {
      log.error("Could not send SMS", e);
    }
  }
}
