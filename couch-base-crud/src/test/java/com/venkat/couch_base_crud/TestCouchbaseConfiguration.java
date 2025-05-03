package com.venkat.couch_base_crud;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestCouchbaseConfiguration {

    @Bean
    @ServiceConnection
    public CouchbaseContainer couchbaseContainer() {
        return new CouchbaseContainer(DockerImageName.parse("couchbase/server:7.6.0"))
                .withBucket(new BucketDefinition("employee")
                        .withPrimaryIndex(true)
                        .withFlushEnabled(false)); // Disable flush for query-based cleanup
    }
}
