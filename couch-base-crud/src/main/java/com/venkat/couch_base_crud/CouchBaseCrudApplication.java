package com.venkat.couch_base_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@SpringBootApplication
public class CouchBaseCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouchBaseCrudApplication.class, args);
	}

}
