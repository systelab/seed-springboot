package com.systelab.seed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

// Check http://www.springboottutorial.com/integration-testing-for-spring-boot-rest-services

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientControllerWebIntegrationTest {

    @LocalServerPort
    private int port;

    @Before
    public void login() {
    }

    @Test
    public void emptyTest() {
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
