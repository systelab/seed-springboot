package com.systelab;

import com.systelab.infraestructure.JWTAuthenticationTokenGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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

    @Autowired
    JWTAuthenticationTokenGenerator tokenGenerator;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Before
    public void login() {
        headers.add("Authorization", "Bearer " + tokenGenerator.issueToken("Systelab", "ADMIN", "http://localhost:" + port));
    }

    @Test
    public void testGetAll() throws IOException {

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<List> response = restTemplate.exchange(createURLWithPort("/seed/v1/patients"), HttpMethod.GET, entity, List.class);
        assertEquals(response.getBody().size(), 0);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}