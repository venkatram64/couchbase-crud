package com.venkat.couch_base_crud;


import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
//@ActiveProfiles("test")
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
    }

}