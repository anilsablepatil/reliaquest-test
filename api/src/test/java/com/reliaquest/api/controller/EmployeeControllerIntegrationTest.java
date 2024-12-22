package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerIntegrationTest {

    @MockBean
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldFetchAllEmployee() throws Exception {
        Mockito.when(employeeService.getAllEmployees()).thenReturn(
                Arrays.asList(
                        new Employee(
                                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                                "test-1",
                                10,
                                30,
                                "HOD",
                                "test1@gmail.com")
                )
        );

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(jsonPath("$[0].name").value("test-1"))
                .andExpect(jsonPath("$[0].salary").value(10))
                .andExpect(jsonPath("$[0].age").value(30))
                .andExpect(jsonPath("$[0].email").value("test1@gmail.com"))
                .andReturn();
    }

    @Test
    public void shouldSearchEmployeeByName() throws Exception {
        Mockito.when(employeeService.findEmployeeByName("test-2")).thenReturn(
                Arrays.asList(
                        new Employee(
                                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                                "test-2",
                                10,
                                30,
                                "HOD-2",
                                "test2@gmail.com")
                )
        );

        mockMvc.perform(get("/search/test-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(jsonPath("$[0].name").value("test-2"))
                .andExpect(jsonPath("$[0].salary").value(10))
                .andExpect(jsonPath("$[0].age").value(30))
                .andExpect(jsonPath("$[0].email").value("test2@gmail.com"))
                .andReturn();
    }

    @Test
    public void shouldFetchEmployeeById() throws Exception {
        Mockito.when(employeeService.getEmployeeById("b888aa78-d601-40a3-81ac-fd536d12dd3a")).thenReturn(
                new Employee(
                        UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                        "test-2",
                        10,
                        30,
                        "HOD-2",
                        "test2@gmail.com")

        );

        mockMvc.perform(get("/b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(jsonPath("$.name").value("test-2"))
                .andExpect(jsonPath("$.salary").value(10))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                .andReturn();
    }

    @Test
    public void shouldFetchHighestSalaryOfEmployee() throws Exception {
        Mockito.when(employeeService.findHighestSalaryOfEmployee()).thenReturn(1000);
        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("1000"));

    }

    @Test
    public void shouldFetchTopTenEarningEmployeeName() throws Exception {
        Mockito.when(employeeService.findTopTenEarningEmployeeNames()).thenReturn(
                Arrays.asList("Test-1", "Test-3")
        );
        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Test-1"))
                .andExpect(jsonPath("$[1]").value("Test-3"));
    }

    @Test
    public void shouldCreateNewEmployee() throws Exception {
        Mockito.when(employeeService.saveEmployee(ArgumentMatchers.any())).thenReturn(
                new Employee(
                        UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                        "test-1",
                        10,
                        30,
                        "HOD",
                        "test1@gmail.com")
        );

        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"test-1\",\n" +
                                "    \"salary\": 10,\n" +
                                "    \"age\": 30,\n" +
                                "    \"title\": \"HOD\"\n" +
                                "}")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(jsonPath("$.name").value("test-1"))
                .andExpect(jsonPath("$.salary").value(10))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("test1@gmail.com"))
                .andReturn();
    }

    @Test
    public void shouldDeleteEmployeeById() throws Exception {
        Mockito.doNothing()
                .when(employeeService)
                .deleteById(UUID.fromString( "b888aa78-d601-40a3-81ac-fd536d12dd3a"));

        mockMvc.perform(delete("/b888aa78-d601-40a3-81ac-fd536d12dd3a"))
                .andExpect(status().isOk())
                .andReturn();
    }
}