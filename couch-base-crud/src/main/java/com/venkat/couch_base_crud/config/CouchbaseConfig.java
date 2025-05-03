package com.venkat.couch_base_crud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;

@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    public String getConnectionString() {
        return "couchbase://localhost";
    }

    @Override
    public String getUserName() {
        return "admin";
    }

    @Override
    public String getPassword() {
        return "admin1234";
    }

    @Override
    public String getBucketName() {
        return "employee";
    }


}
