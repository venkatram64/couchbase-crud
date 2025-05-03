package com.venkat.couch_base_crud;


import com.couchbase.client.java.Cluster;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ImportTestcontainers(TestCouchbaseConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @Autowired
    protected Cluster cluster;

    @BeforeAll
    void setup() {
        // Initial setup if needed
    }

    @AfterEach
    void cleanup() {
        // Clean the entire bucket after each test
        cluster.query("DELETE FROM `employee`");
    }
}