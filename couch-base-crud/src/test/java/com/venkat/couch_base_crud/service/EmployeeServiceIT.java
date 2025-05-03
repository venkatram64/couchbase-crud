package com.venkat.couch_base_crud.service;

import com.venkat.couch_base_crud.BaseIntegrationTest;
import com.venkat.couch_base_crud.exception.CouchbaseOperationException;
import com.venkat.couch_base_crud.exception.EmployeeAlreadyExistsException;
import com.venkat.couch_base_crud.exception.EmployeeNotFoundException;
import com.venkat.couch_base_crud.exception.InvalidEmployeeDataException;
import com.venkat.couch_base_crud.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//integration tests
@SpringBootTest
class EmployeeServiceIT extends BaseIntegrationTest {

	@Autowired
	private EmployeeService employeeService;


	private Employee createTestEmployee() {
		Employee employee = new Employee();
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail(UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + "@test.com");
		employee.setAddress(new String[]{"123 Test St"});
		return employee;
	}

	@Test
	void createEmployee_WithValidData_ShouldReturnSavedEmployee() {
		Employee employee = createTestEmployee();
		Employee saved = employeeService.createEmployee(employee);

		assertNotNull(saved.getId());
		assertEquals(employee.getFirstName(), saved.getFirstName());
	}

	@Test
	void createEmployee_WithDuplicateEmail_ShouldThrowException() {
		Employee employee = createTestEmployee();
		employeeService.createEmployee(employee);

		assertThrows(EmployeeAlreadyExistsException.class, () -> {
			employeeService.createEmployee(employee);
		});
	}

	@Test
	void createEmployee_WithMissingRequiredFields_ShouldThrowException() {
		Employee invalidEmployee = new Employee();
		invalidEmployee.setEmail("invalid@test.com");

		assertThrows(InvalidEmployeeDataException.class, () -> {
			employeeService.createEmployee(invalidEmployee);
		});
	}

	// READ tests
	@Test
	void getEmployeeById_WithValidId_ShouldReturnEmployee() {
		Employee employee = employeeService.createEmployee(createTestEmployee());
		Employee found = employeeService.getEmployeeById(employee.getId());

		assertEquals(employee.getId(), found.getId());
	}

	@Test
	void getEmployeeById_WithInvalidId_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeById("nonexistent-id");
		});
	}

	@Test
	void getAllEmployees_ShouldReturnAllEmployees() {
		employeeService.createEmployee(createTestEmployee());
		employeeService.createEmployee(createTestEmployee());

		List<Employee> employees = employeeService.getAllEmployees();
		assertTrue(employees.size() >= 2);
	}

	@Test
	void getAllEmployees_WhenNoEmployees_ShouldThrowException() {
		assertThrows(CouchbaseOperationException.class, () -> {
			employeeService.getAllEmployees();
		});
	}

	// UPDATE tests
	@Test
	//@RepeatedTest(3)
	void updateEmployee_WithValidData_ShouldUpdateEmployee() {
		Employee original = employeeService.createEmployee(createTestEmployee());
		original.setEmail("updated@example.com");
		original.setFirstName("Updated");

		Employee updated = employeeService.updateEmployee(original.getId(), original);
		assertEquals("Updated", updated.getFirstName());
	}

	@Test
	void updateEmployee_WithNonexistentId_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.updateEmployee("nonexistent-id", createTestEmployee());
		});
	}

	@Test
	void updateEmployee_WithDuplicateEmail_ShouldThrowException() {
		Employee emp1 = employeeService.createEmployee(createTestEmployee());
		Employee emp2 = employeeService.createEmployee(createTestEmployee());

		emp2.setEmail(emp1.getEmail());

		assertThrows(EmployeeAlreadyExistsException.class, () -> {
			employeeService.updateEmployee(emp2.getId(), emp2);
		});
	}

	// DELETE tests
	@Test
	void deleteEmployee_WithValidId_ShouldDeleteEmployee() {
		Employee employee = employeeService.createEmployee(createTestEmployee());
		assertDoesNotThrow(() -> employeeService.deleteEmployee(employee.getId()));

		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeById(employee.getId());
		});
	}

	@Test
	void deleteEmployee_WithInvalidId_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.deleteEmployee("nonexistent-id");
		});
	}

	// Additional query tests
	@Test
	void findByEmail_WithExistingEmail_ShouldReturnEmployee() {
		Employee employee = employeeService.createEmployee(createTestEmployee());
		Employee found = employeeService.getEmployeeByEmail(employee.getEmail());

		assertEquals(employee.getId(), found.getId());
	}

	@Test
	void findByEmail_WithNonexistentEmail_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeByEmail("nonexistent@test.com");
		});
	}

}
