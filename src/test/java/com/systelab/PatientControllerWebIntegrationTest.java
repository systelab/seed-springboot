package com.systelab;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

// Check http://www.springboottutorial.com/integration-testing-for-spring-boot-rest-services

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientControllerWebIntegrationTest {

    @LocalServerPort
    private int port;


    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();

    @Before
    public void login() {
    }

    @Test
    public void testGetAll() throws IOException {
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
