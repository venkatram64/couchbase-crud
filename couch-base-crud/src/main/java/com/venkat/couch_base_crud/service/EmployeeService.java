package com.venkat.couch_base_crud.service;

import com.venkat.couch_base_crud.dto.EmployeeDto;
import com.venkat.couch_base_crud.dto.EmployeeMapper;
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

    public List<EmployeeDto> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            if(employees.isEmpty()) {
                throw new CouchbaseOperationException("No employees found");
            }
            return EmployeeMapper.toDtoList(employees);
        }catch (DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employees");
        }
    }

    public EmployeeDto getEmployeeById(String id) {
        try {
            Employee employee = employeeRepository
                    .findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
            return EmployeeMapper.toDto(employee);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employee");
        }
    }

    public EmployeeDto getEmployeeByEmail(String email) {
        try {
                Employee employee = employeeRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + email + " not found"));
                return EmployeeMapper.toDto(employee);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to fetch employee");
        }
    }

    //creating employee
    public EmployeeDto createEmployee(EmployeeDto employee) {
        Employee emp = EmployeeMapper.toEntity(employee);
        validateEmployeeData(emp);
        try {
            //check if employee with email already exists
            if(employeeRepository.findByEmail(emp.getEmail()).isPresent()) {
                throw new EmployeeAlreadyExistsException("Employee with email " + employee.getEmail() + " already exists");
            }
            Employee savedEmp = employeeRepository.save(emp);
            return EmployeeMapper.toDto(savedEmp);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to create employee");
        }
    }

    public EmployeeDto updateEmployee(String id, EmployeeDto employee) {
        Employee emp = EmployeeMapper.toEntity(employee);
        validateEmployeeData(emp);
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
            if(employeeRepository.findByEmail(emp.getEmail()).isPresent()) {
                throw new EmployeeAlreadyExistsException("Employee with email " + emp.getEmail() + " already exists");
            }
            existingEmployee.setFirstName(emp.getFirstName());
            existingEmployee.setLastName(emp.getLastName());
            existingEmployee.setEmail(emp.getEmail());
            existingEmployee.setAddress(emp.getAddress());
            existingEmployee.setPhones(emp.getPhones());
            Employee updatedEmp = employeeRepository.save(existingEmployee);
            return EmployeeMapper.toDto(updatedEmp);
        }catch(DataAccessException e) {
            throw new CouchbaseOperationException("Unable to update employee");

        }
    }

    public void deleteEmployee(String id) {
        try {
            //employeeRepository.deleteById(id);
            EmployeeDto empDto = getEmployeeById(id);
            Employee employee = EmployeeMapper.toEntity(empDto);
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
        if (employee.getAddress() == null) {
            throw new InvalidEmployeeDataException("At least one address is required");
        }
        if(employee.getPhones() == null || employee.getPhones().isEmpty()) {
            throw new InvalidEmployeeDataException("At least one phone is required");
        }
    }


}
