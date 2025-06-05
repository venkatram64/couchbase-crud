package com.venkat.couch_base_crud;


import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.stream.Collectors;

@SpringBootTest
@Testcontainers
//@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @Autowired
    protected Cluster cluster;

    @Container
    @ServiceConnection
    protected static final CouchbaseContainer couchbaseContainer =
            new CouchbaseContainer(DockerImageName.parse("couchbase/server:community-7.0.2"))
                    .withBucket(new BucketDefinition("mycompany2")
                            .withPrimaryIndex(true)
                            .withFlushEnabled(true))
                    .withStartupTimeout(Duration.ofMinutes(3))
                    .withReuse(true);

    @BeforeAll
    static void waitForClusterReady() {
        // Wait for cluster to be fully ready
        try (Cluster bootstrapCluster = Cluster.connect(
                couchbaseContainer.getConnectionString(),
                couchbaseContainer.getUsername(),
                couchbaseContainer.getPassword()
        )) {
            bootstrapCluster.waitUntilReady(Duration.ofSeconds(30));
            bootstrapCluster.bucket("mycompany2").waitUntilReady(Duration.ofSeconds(30));
        }
    }

    @AfterEach
    void cleanup() {
        // Clean the entire bucket after each test
        try {
            cluster.query("DELETE FROM `employee` WHERE meta().id IS NOT MISSING");
            //QueryResult query = cluster.query("DELETE FROM `employee` WHERE meta().id IS NOT MISSING");
            Thread.sleep(500);
            //query.rowsAsObject().forEach(obj ->System.out.println("Deleted: " + obj));
        } catch(Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void verifyCleanState() {
        long count = cluster.query("SELECT COUNT(*) AS count FROM `employee`")
                .rowsAsObject()
                .get(0)
                .getLong("count");

        if (count > 0) {
            System.err.println("WARNING: Bucket not clean - contains " + count + " documents");
        }

        System.out.println("Connected to Couchbase at:");
        cluster.environment().ioConfig().mutationTokensEnabled(); // Just to access the env

    }

}