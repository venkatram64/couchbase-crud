package com.venkat.couch_base_crud.repository;

import com.venkat.couch_base_crud.model.Employee;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends CouchbaseRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByFirstName(String firstName);
}
