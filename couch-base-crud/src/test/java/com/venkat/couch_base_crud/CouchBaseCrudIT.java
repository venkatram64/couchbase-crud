package com.venkat.couch_base_crud;

import com.venkat.couch_base_crud.exception.EmployeeNotFoundException;
import com.venkat.couch_base_crud.model.Employee;
import com.venkat.couch_base_crud.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

//integration tests
@SpringBootTest
class CouchBaseCrudIT extends BaseIntegrationTest{

	@Autowired
	private EmployeeService employeeService;

	@Test
	void shouldSaveAndRetrieveEmployee() {
		Employee employee = new Employee();
		employee.setFirstName("Test");
		employee.setLastName("Container");
		employee.setEmail("test@container.com");
		employee.setAddress(new String[]{"123 TC Street"});

		Employee saved = employeeService.createEmployee(employee);
		Employee found = employeeService.getEmployeeById(saved.getId());

		assertNotNull(saved.getId());
		assertEquals("Test", found.getFirstName());
		assertEquals("Container", found.getLastName());
	}

	@Test
	void shouldFailWhenEmployeeNotFound() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeById("non-existent-id");
		});
	}

}
