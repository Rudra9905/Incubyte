package com.incubyte.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Integration tests use the 'test' profile with in-memory H2 database.
    // This allows quick and reliable tests without requiring Docker.
}
