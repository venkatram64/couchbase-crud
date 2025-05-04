package com.venkat.couch_base_crud.controller;

import com.venkat.couch_base_crud.exception.*;
import com.venkat.couch_base_crud.model.Employee;
import com.venkat.couch_base_crud.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@WebMvcTest(EmployeeController.class)
@Import({ GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private final String BASE_URL = "/employee/v1";
    private final String EMPLOYEE_ID = UUID.randomUUID().toString();

    private final Employee mockEmployee = new Employee(
            EMPLOYEE_ID,
            "John",
            "Doe",
            "john.doe@example.com",
            new String[]{"123 Main St"});

    @Test
    void getAllEmployees_ShouldReturnEmployees() throws Exception {

        given(employeeService.getAllEmployees())
                .willReturn(Arrays.asList(mockEmployee));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getAllEmployees_WhenEmpty_ShouldReturnEmptyList() throws Exception {

        given(employeeService.getAllEmployees())
                .willReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() throws Exception {
        given(employeeService.getEmployeeById(EMPLOYEE_ID))
                .willReturn(mockEmployee);

        mockMvc.perform(get(BASE_URL + "/{id}", EMPLOYEE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(employeeService).getEmployeeById(EMPLOYEE_ID);
    }

    @Test
    void getEmployeeById_WhenNotFound_ShouldReturn404() throws Exception {
        given(employeeService.getEmployeeById("nonexistent-id"))
                .willThrow(new EmployeeNotFoundException("Not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", "nonexistent-id"))
                .andExpect(status().isNotFound());

        verify(employeeService).getEmployeeById("nonexistent-id");
    }

    @Test
    void createEmployee_ShouldReturnCreated() throws Exception {
        given(employeeService.createEmployee(any(Employee.class)))
                .willReturn(mockEmployee);

        String employeeJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "address": ["123 Main St"]
        }
        """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EMPLOYEE_ID));

        verify(employeeService).createEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_WithInvalidData_ShouldReturn400() throws Exception {
        String invalidEmployeeJson = """
        {
            "lastName": "Doe",
            "email": "invalid-email"
        }
        """;
        given(employeeService.createEmployee(any(Employee.class)))
                .willThrow(new InvalidEmployeeDataException("Invalid data"));;

        mockMvc.perform(post("/employee/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmployeeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        Employee updatedEmployee = new Employee(
                EMPLOYEE_ID,
                "John",
                "Updated",
                "john.updated@example.com",
                new String[]{"456 New St"});

        given(employeeService.updateEmployee(eq(EMPLOYEE_ID), any(Employee.class)))
                .willReturn(updatedEmployee);

        String updateJson = """
        {
            "firstName": "John",
            "lastName": "Updated",
            "email": "john.updated@example.com",
            "address": ["456 New St"]
        }
        """;

        mockMvc.perform(put(BASE_URL + "/{id}", EMPLOYEE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        verify(employeeService).updateEmployee(eq(EMPLOYEE_ID), any(Employee.class));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(EMPLOYEE_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", EMPLOYEE_ID))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(EMPLOYEE_ID);
    }

    @Test
    void deleteEmployee_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new EmployeeNotFoundException("Not found"))
                .when(employeeService).deleteEmployee("nonexistent-id");

        mockMvc.perform(delete(BASE_URL + "/{id}", "nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleEmployeeAlreadyExistsException() throws Exception {
        given(employeeService.createEmployee(any(Employee.class)))
                .willThrow(new EmployeeAlreadyExistsException("Email exists"));

        String employeeJson = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "existing@example.com",
            "address": ["123 Main St"]
        }
        """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email exists"));
    }

    @Test
    void handleInvalidEmployeeDataException() throws Exception {
        given(employeeService.createEmployee(any(Employee.class)))
                .willThrow(new InvalidEmployeeDataException("Invalid data"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

}