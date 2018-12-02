package com.texastoc.cucumber;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringBootBaseIntegrationTest {

    private final String SERVER_URL = "http://localhost";
    private String V2_ENDPOINT;

    @LocalServerPort
    private int port;

    protected RestTemplate restTemplate;

    public SpringBootBaseIntegrationTest() {
        restTemplate = new RestTemplate();
    }

    protected String endpoint() {
        if (V2_ENDPOINT == null) {
            V2_ENDPOINT = SERVER_URL + ":" + port + "/api/v2";
        }
        return V2_ENDPOINT;
    }

}
