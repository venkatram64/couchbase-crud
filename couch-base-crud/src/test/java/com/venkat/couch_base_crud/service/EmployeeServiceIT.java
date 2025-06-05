package com.venkat.couch_base_crud.service;

import com.venkat.couch_base_crud.BaseIntegrationTest;
import com.venkat.couch_base_crud.dto.AddressDto;
import com.venkat.couch_base_crud.dto.EmployeeDto;
import com.venkat.couch_base_crud.dto.PhoneDto;
import com.venkat.couch_base_crud.exception.CouchbaseOperationException;
import com.venkat.couch_base_crud.exception.EmployeeAlreadyExistsException;
import com.venkat.couch_base_crud.exception.EmployeeNotFoundException;
import com.venkat.couch_base_crud.exception.InvalidEmployeeDataException;
import com.venkat.couch_base_crud.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//integration tests
@SpringBootTest
class EmployeeServiceIT extends BaseIntegrationTest {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EmployeeRepository employeeRepository;


	private EmployeeDto createTestEmployee() {
		AddressDto mockAddress = new AddressDto("123 Main St", "New York", "NY", "10001");
		PhoneDto mockPhone = new PhoneDto("home", "123-456-7890");
		List<PhoneDto> mockPhoneList = Arrays.asList(mockPhone);

		EmployeeDto employee = new EmployeeDto();
		employee.setFirstName("John");
		employee.setLastName("Doe");
		employee.setEmail(UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + "@test.com");
		employee.setAddress(mockAddress);
		employee.setPhones(mockPhoneList);
		return employee;
	}

	@Test
	void createEmployee_WithValidData_ShouldReturnSavedEmployee() {
		EmployeeDto employee = createTestEmployee();
		EmployeeDto saved = employeeService.createEmployee(employee);

		assertNotNull(saved.getId());
		assertEquals(employee.getFirstName(), saved.getFirstName());
	}

	@Test
	void createEmployee_WithDuplicateEmail_ShouldThrowException() {
		EmployeeDto employee = createTestEmployee();
		employeeService.createEmployee(employee);

		assertThrows(EmployeeAlreadyExistsException.class, () -> {
			employeeService.createEmployee(employee);
		});
	}

	@Test
	void createEmployee_WithMissingRequiredFields_ShouldThrowException() {
		EmployeeDto invalidEmployee = new EmployeeDto();
		invalidEmployee.setEmail("invalid@test.com");

		assertThrows(InvalidEmployeeDataException.class, () -> {
			employeeService.createEmployee(invalidEmployee);
		});
	}

	// READ tests
	@Test
	void getEmployeeById_WithValidId_ShouldReturnEmployee() {
		EmployeeDto employee = employeeService.createEmployee(createTestEmployee());
		EmployeeDto found = employeeService.getEmployeeById(employee.getId());

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
		employeeRepository.deleteAll();
		EmployeeDto empDto = employeeService.createEmployee(createTestEmployee());
		EmployeeDto empDto2 = employeeService.createEmployee(createTestEmployee());

		List<EmployeeDto> employees = employeeService.getAllEmployees();
		assertTrue(employees.size() >= 2);
	}

	@Test
	void getAllEmployees_WhenNoEmployees_ShouldThrowException() {
		employeeRepository.deleteAll();
		assertThrows(CouchbaseOperationException.class, () -> {
			employeeService.getAllEmployees();
		});
	}

	// UPDATE tests
	@Test
	//@RepeatedTest(3)
	void updateEmployee_WithValidData_ShouldUpdateEmployee() {
		EmployeeDto original = employeeService.createEmployee(createTestEmployee());
		original.setEmail("updated@example.com");
		original.setFirstName("Updated");

		EmployeeDto updated = employeeService.updateEmployee(original.getId(), original);
		assertEquals("Updated", updated.getFirstName());
	}

	@Test
	void updateEmployee_WithNonexistentId_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.updateEmployee("nonexistent-id", createTestEmployee());
		});
	}


	// DELETE tests
	@Test
	void deleteEmployee_WithValidId_ShouldDeleteEmployee() {
		EmployeeDto employee = employeeService.createEmployee(createTestEmployee());
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
		EmployeeDto employee = employeeService.createEmployee(createTestEmployee());
		EmployeeDto found = employeeService.getEmployeeByEmail(employee.getEmail());

		assertEquals(employee.getId(), found.getId());
	}

	@Test
	void findByEmail_WithNonexistentEmail_ShouldThrowException() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeByEmail("nonexistent@test.com");
		});
	}

}
