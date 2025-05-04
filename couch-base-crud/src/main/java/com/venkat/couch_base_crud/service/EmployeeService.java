package com.venkat.couch_base_crud.service;

import com.venkat.couch_base_crud.exception.CouchbaseOperationException;
import com.venkat.couch_base_crud.exception.EmployeeAlreadyExistsException;
import com.venkat.couch_base_crud.exception.EmployeeNotFoundException;
import com.venkat.couch_base_crud.exception.InvalidEmployeeDataException;
import com.venkat.couch_base_crud.model.Employee;
import com.venkat.couch_base_crud.repository.EmployeeRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            if(employees.isEmpty()) {
                throw new CouchbaseOperationException("No employees found");
            }
            return employees;
        }catch (DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employees");
        }
    }

    public Employee getEmployeeById(String id) {
        try {
            return employeeRepository
                    .findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employee");
        }
    }

    public Employee getEmployeeByEmail(String email) {
        try {
            return employeeRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + email + " not found"));
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employee");
        }
    }

    //creating employee
    public Employee createEmployee(Employee employee) {
        validateEmployeeData(employee);
        try {
            //check if employee with email already exists
            if(employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
                throw new EmployeeAlreadyExistsException("Employee with email " + employee.getEmail() + " already exists");
            }
            return employeeRepository.save(employee);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to create employee");
        }
    }

    public Employee updateEmployee(String id, Employee employee) {
        validateEmployeeData(employee);
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
            if(employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
                throw new EmployeeAlreadyExistsException("Employee with email " + employee.getEmail() + " already exists");
            }
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setAddress(employee.getAddress());
            return employeeRepository.save(existingEmployee);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to update employee");

        }
    }

    public void deleteEmployee(String id) {
        try {
            //employeeRepository.deleteById(id);
            Employee employee = getEmployeeById(id);
            employeeRepository.delete(employee);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to delete employee");
        }
    }


    private void validateEmployeeData(Employee employee) {
        if (!StringUtils.hasText(employee.getFirstName())) {
            throw new InvalidEmployeeDataException("First name is required");
        }
        if (!StringUtils.hasText(employee.getLastName())) {
            throw new InvalidEmployeeDataException("Last name is required");
        }
        if (!StringUtils.hasText(employee.getEmail()) || !employee.getEmail().contains("@")) {
            throw new InvalidEmployeeDataException("Email is required");
        }
        if (employee.getAddress() == null || employee.getAddress().length == 0) {
            throw new InvalidEmployeeDataException("At least one address is required");
        }
    }


}
