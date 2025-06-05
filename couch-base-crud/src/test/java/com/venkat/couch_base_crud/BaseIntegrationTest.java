package com.venkat.couch_base_crud;


import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
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
import java.time.Instant;
import java.util.stream.Collectors;

@SpringBootTest
@Testcontainers
//@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    protected static final String BUCKET_NAME = "mycompany";
    private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(2);

    private static CouchbaseContainer couchbaseContainer;
    private static String connectionString;
    private static String username;
    private static String password;

    @DynamicPropertySource
    static void couchbaseProperties(DynamicPropertyRegistry registry) {
        initializeContainer();

        registry.add("spring.couchbase.connection-string", () -> connectionString);
        registry.add("spring.couchbase.username", () -> username);
        registry.add("spring.couchbase.password", () -> password);
        registry.add("spring.data.couchbase.bucket-name", () -> BUCKET_NAME);
    }

    @Autowired
    protected Cluster cluster;

    private static synchronized void initializeContainer() {
        if (couchbaseContainer == null) {
            couchbaseContainer = new CouchbaseContainer(DockerImageName.parse("couchbase/server:community-7.1.1"))
                    .withBucket(new BucketDefinition(BUCKET_NAME)
                            .withPrimaryIndex(true)
                            .withFlushEnabled(true))
                    .withStartupTimeout(STARTUP_TIMEOUT)
                    .withReuse(false);

            couchbaseContainer.start();
            connectionString = couchbaseContainer.getConnectionString();
            username = couchbaseContainer.getUsername();
            password = couchbaseContainer.getPassword();

            initializeCluster();
        }
    }

    private static void initializeCluster() {
        Cluster bootstrapCluster = Cluster.connect(
                connectionString,
                username,
                password
        );

        try {
            waitForClusterReady(bootstrapCluster);
            initializeBucket(bootstrapCluster);
        } finally {
            bootstrapCluster.disconnect();
        }
    }

    private static void waitForClusterReady(Cluster cluster) {
        Instant start = Instant.now();
        while (Duration.between(start, Instant.now()).compareTo(STARTUP_TIMEOUT) < 0) {
            try {
                cluster.ping();
                return;
            } catch (Exception e) {
                sleep(1000);
                System.out.println("Waiting for cluster to be ready...");
            }
        }
        throw new IllegalStateException("Cluster not ready after " + STARTUP_TIMEOUT);
    }

    private static void initializeBucket(Cluster cluster) {
        Bucket bucket = cluster.bucket(BUCKET_NAME);

        // First check if bucket exists
        waitForBucketExists(bucket);

        // Then wait for bucket to be ready
        waitForBucketReady(bucket);

        // Create primary index
        createPrimaryIndex(cluster);
    }

    private static void waitForBucketExists(Bucket bucket) {
        Instant start = Instant.now();
        while (Duration.between(start, Instant.now()).compareTo(STARTUP_TIMEOUT) < 0) {
            try {
                bucket.ping();
                return;
            } catch (BucketNotFoundException e) {
                sleep(1000);
                System.out.println("Waiting for bucket to exist...");
            } catch (Exception e) {
                sleep(1000);
                System.out.println("Waiting for bucket service...");
            }
        }
        throw new IllegalStateException("Bucket not found after " + STARTUP_TIMEOUT);
    }

    private static void waitForBucketReady(Bucket bucket) {
        Instant start = Instant.now();
        while (Duration.between(start, Instant.now()).compareTo(STARTUP_TIMEOUT) < 0) {
            try {
                bucket.waitUntilReady(Duration.ofSeconds(10));
                return;
            } catch (Exception e) {
                sleep(1000);
                System.out.println("Waiting for bucket to be ready...");
            }
        }
        throw new IllegalStateException("Bucket not ready after " + STARTUP_TIMEOUT);
    }

    private static void createPrimaryIndex(Cluster cluster) {
        try {
            cluster.query("CREATE PRIMARY INDEX ON `" + BUCKET_NAME + "` IF NOT EXISTS");
        } catch (Exception e) {
            System.err.println("Warning: Primary index creation failed - " + e.getMessage());
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @BeforeEach
    void verifyConnection() {
        try {
            Bucket bucket = cluster.bucket(BUCKET_NAME);
            //System.out.println("Bucket status: " + bucket.status());

            // More reliable readiness check
            if (bucket.ping().endpoints().isEmpty()) {
                throw new IllegalStateException("Bucket not properly connected");
            }
        } catch (Exception e) {
            System.err.println("Connection verification failed: " + e.getMessage());
            printClusterDiagnostics();
        }
    }

    private void printClusterDiagnostics() {
        try {
            System.out.println("Cluster diagnostics:");
            System.out.println(cluster.diagnostics());
        } catch (Exception e) {
            System.err.println("Failed to get diagnostics: " + e.getMessage());
        }
    }

    @AfterEach
    void cleanup() {
        try {
            cluster.query("DELETE FROM `" + BUCKET_NAME + "`");
        } catch(Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

}