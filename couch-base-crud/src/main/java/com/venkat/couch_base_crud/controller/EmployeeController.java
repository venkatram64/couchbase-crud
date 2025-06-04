package com.venkat.couch_base_crud.controller;

import com.venkat.couch_base_crud.dto.EmployeeDto;
import com.venkat.couch_base_crud.model.Employee;
import com.venkat.couch_base_crud.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("v1")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping("/v1")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employee) {
        return new ResponseEntity<>(employeeService.createEmployee(employee), HttpStatus.CREATED);
    }

    @PutMapping("/v1/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable String id, @RequestBody EmployeeDto employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping("/v1/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
