package com.texastoc.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class EmailConnector {

  private static final int TIMEOUT = 10000;
  private static RequestConfig requestConfig;
  private ExecutorService executorService;
  private final String apiKey;

  public EmailConnector(@Value("${postmarkapp.key:POSTMARK_API_TEST}") String apiKey) {
    this.apiKey = apiKey;
    executorService = Executors.newCachedThreadPool();
  }

  public void send(String email, String subject, String body) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("From: 'info@texastoc.com',");
    sb.append("To: '" + email + "',");
    sb.append("Subject: '" + subject + "',");
    sb.append("HtmlBody: '");
    sb.append(body);
    sb.append("'");
    sb.append("}");

    executorService.submit(new EmailSender(sb.toString()));
  }

  // TODO threading
  private void send(String emailPayload) {
  }

  private class EmailSender implements Callable<Void> {
    private String emailPayload;

    public EmailSender(String emailPayload) {
      this.emailPayload = emailPayload;
    }

    @Override
    public Void call() throws Exception {
      log.info("Attemping to send email " + emailPayload);

      try {
        CloseableHttpClient client = HttpClientBuilder
          .create()
          .setDefaultRequestConfig(httpClientConfig())
          .build();
        HttpPost post = new HttpPost("https://api.postmarkapp.com/email");
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("X-Postmark-Server-Token", apiKey);
        StringEntity payload = new StringEntity(emailPayload, "UTF-8");
        post.setEntity(payload);

        try {
          client.execute(post);
        } catch (HttpResponseException hre) {
          switch (hre.getStatusCode()) {
            case 401:
            case 422:
              log.warn("There was a problem with the email: "
                + hre.getMessage());
              break;
            case 500:
              log.warn("There has been an error sending your email: "
                + hre.getMessage());
              break;
            default:
              log.warn("There has been an unknown error sending your email: "
                + hre.getMessage());
          }
        }
      } catch (Exception e) {
        log.error("Could not send email " + e);
      }
      return null;
    }
  }

  private static RequestConfig httpClientConfig() {
    if (requestConfig == null) {
      requestConfig = RequestConfig.custom()
        .setConnectTimeout(TIMEOUT)
        .setConnectionRequestTimeout(TIMEOUT)
        .setSocketTimeout(TIMEOUT)
        .build();
    }
    return requestConfig;
  }
}
