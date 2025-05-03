package com.venkat.couch_base_crud.exception;

public class CouchbaseOperationException extends RuntimeException {
    public CouchbaseOperationException(String message) {
        super(message);
    }
}
