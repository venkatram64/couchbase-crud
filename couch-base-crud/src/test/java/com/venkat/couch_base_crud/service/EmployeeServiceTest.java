package com.venkat.couch_base_crud.service;

import com.venkat.couch_base_crud.exception.CouchbaseOperationException;
import com.venkat.couch_base_crud.exception.EmployeeAlreadyExistsException;
import com.venkat.couch_base_crud.exception.EmployeeNotFoundException;
import com.venkat.couch_base_crud.exception.InvalidEmployeeDataException;
import com.venkat.couch_base_crud.model.Employee;
import com.venkat.couch_base_crud.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee validEmployee;
    private Employee existingEmployee;

    @BeforeEach
    void setUp() {
        validEmployee = new Employee(
                "1", "John", "Doe", "john.doe@example.com",
                new String[]{"123 Main St"});

        existingEmployee = new Employee(
                "2", "Jane", "Smith", "jane.smith@example.com",
                new String[]{"456 Oak Ave"});
    }

    @Test
    void createEmployee_WithValidData_ShouldReturnSavedEmployee() {
        // Arrange
        when(employeeRepository.findByEmail(validEmployee.getEmail()))
                .thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(validEmployee);

        // Act
        Employee result = employeeService.createEmployee(validEmployee);

        // Assert
        assertNotNull(result);
        assertEquals(validEmployee.getEmail(), result.getEmail());
        verify(employeeRepository, times(1)).save(validEmployee);
    }

    @Test
    void createEmployee_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findByEmail(existingEmployee.getEmail()))
                .thenReturn(Optional.of(existingEmployee));

        // Act & Assert
        assertThrows(EmployeeAlreadyExistsException.class, () -> {
            employeeService.createEmployee(existingEmployee);
        });
    }

    @Test
    void createEmployee_WithInvalidData_ShouldThrowException() {
        // Arrange
        Employee invalidEmployee = new Employee();
        invalidEmployee.setEmail("invalid-email");

        // Act & Assert
        assertThrows(InvalidEmployeeDataException.class, () -> {
            employeeService.createEmployee(invalidEmployee);
        });
    }

    //get employee

    @Test
    void getEmployeeById_WithValidId_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById("1"))
                .thenReturn(Optional.of(validEmployee));

        // Act
        Employee result = employeeService.getEmployeeById("1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void getEmployeeById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findById("999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("999");
        });
    }

    //update

    @Test
    void updateEmployee_WithValidData_ShouldReturnUpdatedEmployee() {
        // Arrange
        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Updated");
        updatedDetails.setLastName("Name");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setAddress(new String[]{"New Address"});

        when(employeeRepository.findById("1"))
                .thenReturn(Optional.of(validEmployee));
        /*when(employeeRepository.findByEmail("updated@example.com"))
                .thenReturn(Optional.empty());*/
        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(updatedDetails);

        // Act
        Employee result = employeeService.updateEmployee("1", updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void updateEmployee_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Updated");
        updatedDetails.setLastName("Name");
        updatedDetails.setEmail("jane.smith@example.com"); // Existing email
        updatedDetails.setAddress(new String[]{"New Address"});

        when(employeeRepository.findById("1"))
                .thenReturn(Optional.of(validEmployee));
        when(employeeRepository.findByEmail("jane.smith@example.com"))
                .thenReturn(Optional.of(existingEmployee));

        // Act & Assert
        assertThrows(EmployeeAlreadyExistsException.class, () -> {
            employeeService.updateEmployee("1", updatedDetails);
        });
    }

    //delete employee, failed

    @Test
    void deleteEmployee_WithValidId_ShouldDeleteSuccessfully() {
        // Arrange
        when(employeeRepository.findById("1"))
                .thenReturn(Optional.of(validEmployee));
        doNothing().when(employeeRepository).delete(validEmployee);

        // Act
        employeeService.deleteEmployee("1");

        // Assert
        verify(employeeRepository, times(1)).delete(validEmployee);
    }

    @Test
    void deleteEmployee_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findById("999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployee("999");
        });
    }

    //find by email

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(validEmployee));

        // Act
        Employee result = employeeService.getEmployeeByEmail("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeByEmail("nonexistent@example.com");
        });
    }

    //edge case tests

    @Test
    void getAllEmployees_WhenNoEmployeesExist_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThrows(CouchbaseOperationException.class, () -> {
            employeeService.getAllEmployees();
        });
    }

    @Test
    void getAllEmployees_WhenEmployeesExist_ShouldReturnList() {
        // Arrange
        when(employeeRepository.findAll())
                .thenReturn(Arrays.asList(validEmployee, existingEmployee));

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void createEmployee_WhenDatabaseErrorOccurs_ShouldThrowCouchbaseException() {
        // Arrange
        when(employeeRepository.findByEmail(validEmployee.getEmail()))
                .thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class)))
                .thenThrow(new CouchbaseOperationException("DB Error"));

        // Act & Assert
        assertThrows(CouchbaseOperationException.class, () -> {
            employeeService.createEmployee(validEmployee);
        });
    }



    private Employee createTestEmployee(String id, String firstName, String lastName,
                                        String email, String[] address) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setAddress(address);
        return employee;
    }


}