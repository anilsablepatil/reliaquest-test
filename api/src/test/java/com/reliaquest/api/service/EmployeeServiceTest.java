package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.client.inbound.CreateEmployeeApiRequest;
import com.reliaquest.api.client.inbound.DeleteEmployeeApiRequest;
import com.reliaquest.api.client.outbound.EmployeeApiResponse;
import com.reliaquest.api.controller.inbound.CreateEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private EmployeeApiClient employeeApiClient;
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        employeeApiClient = Mockito.mock(EmployeeApiClient.class);
        employeeService = new EmployeeService(employeeApiClient);
    }

    @Test
    public void shouldFetchAllEmployee() {
        EmployeeApiResponse employee1 = new EmployeeApiResponse(
                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test1",
                10,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee3 = new EmployeeApiResponse(
                UUID.fromString("d888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test3",
                30,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(
                Arrays.asList(
                        employee1,
                        employee2,
                        employee3
                )
        );
        List<Employee> actualEmployee = employeeService.getAllEmployees();
        Assertions.assertThat(actualEmployee).containsExactlyInAnyOrder(
                employee1.toEmployee(),
                employee2.toEmployee(),
                employee3.toEmployee()
        );
    }

    @Test
    public void shouldFindEmployeeByName() {
        EmployeeApiResponse employee1 = new EmployeeApiResponse(
                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test1",
                10,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee3 = new EmployeeApiResponse(
                UUID.fromString("d888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test3",
                30,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(
                Arrays.asList(
                        employee1,
                        employee2,
                        employee3
                )
        );
        List<Employee> actualEmployee = employeeService.findEmployeeByName("Test2");
        Assertions.assertThat(actualEmployee).containsExactlyInAnyOrder(
                employee2.toEmployee()
        );
    }

    @Test
    public void shouldGetEmployeeById() {
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findById(UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a")))
                .thenReturn(employee2);

        Employee actualEmployee = employeeService.getEmployeeById("c888aa78-d601-40a3-81ac-fd536d12dd3a");
        Assertions.assertThat(actualEmployee).isEqualTo(
                employee2.toEmployee()
        );
    }

    @Test
    public void shouldFindHighestSalaryOfEmployee() {
        EmployeeApiResponse employee1 = new EmployeeApiResponse(
                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test1",
                10,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee3 = new EmployeeApiResponse(
                UUID.fromString("d888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test3",
                30,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(
                Arrays.asList(
                        employee1,
                        employee2,
                        employee3
                )
        );
        Integer highestSalaryOfEmployee = employeeService.findHighestSalaryOfEmployee();
        Assertions.assertThat(highestSalaryOfEmployee).isEqualTo(30);
    }

    @Test
    public void shouldFindTopTenEarningEmployeeNames() {
        List<EmployeeApiResponse> listOfEmployees = IntStream.range(1, 51)
                .mapToObj(i -> new EmployeeApiResponse(
                        UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                        "Test-" + i,
                        10 * i,
                        30,
                        "title",
                        "email")
                ).collect(Collectors.toList());

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(listOfEmployees);

        List<String> topTenEmployee = employeeService.findTopTenEarningEmployeeNames();
        Assertions.assertThat(topTenEmployee).containsExactlyInAnyOrder(
             "Test-41",
             "Test-42",
             "Test-43",
             "Test-44",
             "Test-45",
             "Test-46",
             "Test-47",
             "Test-48",
             "Test-49",
             "Test-50"
        );
    }

    @Test
    public void shouldSaveEmployee() {
        String name = "Test1";
        int salary = 10;
        int age = 30;
        String title = "title";
        UUID id = UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a");

        Mockito.when(employeeApiClient.createEmployee(
                new CreateEmployeeApiRequest(name, salary, age, title))
        ).thenReturn(
                new EmployeeApiResponse(
                        id, name, salary, age, title, "email")
        );

        Employee actualEmployee = employeeService.saveEmployee(
                new CreateEmployeeRequest(name, salary, age, title));

        Assertions.assertThat(actualEmployee).isEqualTo(
                new Employee(id, name, salary, age, title, "email"
                )
        );
    }

    @Test
    public void shouldDeleteById() {
        EmployeeApiResponse employee1 = new EmployeeApiResponse(
                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test1",
                10,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee3 = new EmployeeApiResponse(
                UUID.fromString("d888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test3",
                30,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(
                Arrays.asList(employee1, employee2, employee3));

        employeeService.deleteById(UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"));

        Mockito.verify(employeeApiClient).deleteEmployee(
                new DeleteEmployeeApiRequest("Test2")
        );
    }

    @Test
    public void shouldThrowEmployeeNotFoundExceptionWhenEmployeeTobDeletedNotPresent() {
        EmployeeApiResponse employee1 = new EmployeeApiResponse(
                UUID.fromString("b888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test1",
                10,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee2 = new EmployeeApiResponse(
                UUID.fromString("c888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test2",
                20,
                30,
                "title",
                "email"
        );
        EmployeeApiResponse employee3 = new EmployeeApiResponse(
                UUID.fromString("d888aa78-d601-40a3-81ac-fd536d12dd3a"),
                "Test3",
                30,
                30,
                "title",
                "email"
        );

        Mockito.when(employeeApiClient.findAllEmployee()).thenReturn(
                Arrays.asList(employee1, employee2, employee3));

        Assertions.assertThatThrownBy(() ->
                employeeService.deleteById(UUID.fromString("e888aa78-d601-40a3-81ac-fd536d12dd3a"))
        ).isInstanceOf(EmployeeNotFoundException.class);
    }
}