package com.systelab.seed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Check http://www.springboottutorial.com/integration-testing-for-spring-boot-rest-services

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class PatientControllerWebIntegrationTest {

    @BeforeEach
    public void login() {
    }

    @Test
    public void emptyTest() {
    }
}
