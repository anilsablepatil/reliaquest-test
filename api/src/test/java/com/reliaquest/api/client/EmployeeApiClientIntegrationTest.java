package com.reliaquest.api.client;

import com.reliaquest.api.client.inbound.CreateEmployeeApiRequest;
import com.reliaquest.api.client.inbound.DeleteEmployeeApiRequest;
import com.reliaquest.api.client.outbound.EmployeeApiResponse;
import com.reliaquest.api.config.EmployeeServiceConfig;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        EmployeeApiClient.class,
        EmployeeServiceConfig.class})
@TestPropertySource(properties = "EMPLOYEE_SERVICE_BASE_PATH=http://localhost:5353")
public class EmployeeApiClientIntegrationTest {

    private static ClientAndServer mockServer;

    @Autowired
    private EmployeeApiClient employeeApiClient;

    @BeforeAll
    static public void setupMockServer() {
        mockServer = new ClientAndServer(5353);
    }

    @AfterAll
    static public void destroyMockServer() {
        mockServer.stop();
    }

    @BeforeEach
    public void resetMockServer() {
        mockServer.reset();
    }

    @Test
    public void shouldCreateEmployeeInExternalSystem() {
        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/api/v1/employee")
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                                "{\n" +
                                        "      \"name\" : \"Test Employee\",\n" +
                                        "      \"salary\" : 10000,\n" +
                                        "      \"age\" : 22,\n" +
                                        "      \"title\" : \"Test\"\n" +
                                        "    }"
                        )
                )
        ).respond(HttpResponse.response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                                "{\n" +
                                        "    \"data\": {\n" +
                                        "        \"id\": \"788b9bd5-4c81-470d-b52a-50e443509283\",\n" +
                                        "        \"employee_name\": \"Test Employee\",\n" +
                                        "        \"employee_salary\": 10000,\n" +
                                        "        \"employee_age\": 22,\n" +
                                        "        \"employee_title\": \"Test\",\n" +
                                        "        \"employee_email\": \"bamity@company.com\"\n" +
                                        "    },\n" +
                                        "    \"status\": \"Successfully processed request.\"\n" +
                                        "}"
                        )
                )
        );

        String employeeName = "Test Employee";
        Integer employeeSalary = 10000;
        Integer employeeAge = 22;
        String employeeTitle = "Test";

        CreateEmployeeApiRequest createEmployeeApiRequest = new CreateEmployeeApiRequest(
                employeeName, employeeSalary, employeeAge, employeeTitle
        );

        EmployeeApiResponse actualEmployee = employeeApiClient.createEmployee(createEmployeeApiRequest);

        Assertions.assertThat(actualEmployee.id()).isEqualTo(UUID.fromString("788b9bd5-4c81-470d-b52a-50e443509283"));
        Assertions.assertThat(actualEmployee.name()).isEqualTo(employeeName);
        Assertions.assertThat(actualEmployee.age()).isEqualTo(employeeAge);
        Assertions.assertThat(actualEmployee.salary()).isEqualTo(employeeSalary);
        Assertions.assertThat(actualEmployee.title()).isEqualTo(employeeTitle);
        Assertions.assertThat(actualEmployee.email()).isEqualTo("bamity@company.com");
    }

    @Test
    public void shouldListAllEmployee() {
        mockServer.when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api/v1/employee")
        ).respond(HttpResponse.response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                        "{\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "            \"id\": \"e50c4c20-88bb-43b3-87a9-aab952916a76\",\n" +
                                "            \"employee_name\": \"Scotty Frami DDS\",\n" +
                                "            \"employee_salary\": 381603,\n" +
                                "            \"employee_age\": 63,\n" +
                                "            \"employee_title\": \"Administration Facilitator\",\n" +
                                "            \"employee_email\": \"tickleme_pink@company.com\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"id\": \"ad0cb745-1c7a-4682-a99a-8bcaa6793c35\",\n" +
                                "            \"employee_name\": \"Russell DuBuque V\",\n" +
                                "            \"employee_salary\": 259185,\n" +
                                "            \"employee_age\": 43,\n" +
                                "            \"employee_title\": \"National Real-Estate Producer\",\n" +
                                "            \"employee_email\": \"magik_mike@company.com\"\n" +
                                "        }" +
                                "],\n" +
                                "    \"status\": \"Successfully processed request.\"" +
                                "}"
                        )
                )
        );

        List<EmployeeApiResponse> allEmployee = employeeApiClient.findAllEmployee();

        Assertions.assertThat(allEmployee)
                .containsExactlyInAnyOrder(
                        new EmployeeApiResponse(
                                UUID.fromString("e50c4c20-88bb-43b3-87a9-aab952916a76"),
                                "Scotty Frami DDS",
                                381603,
                                63,
                                "Administration Facilitator",
                                "tickleme_pink@company.com"),
                        new EmployeeApiResponse(
                                UUID.fromString("ad0cb745-1c7a-4682-a99a-8bcaa6793c35"),
                                "Russell DuBuque V",
                                259185,
                                43,
                                "National Real-Estate Producer",
                                "magik_mike@company.com")
                );
    }

    @Test
    void shouldFindEmployeeById() {
        mockServer.when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api/v1/employee/e50c4c20-88bb-43b3-87a9-aab952916a76")
        ).respond(HttpResponse.response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                            "{\n" +
                                    "    \"data\": {\n" +
                                    "        \"id\": \"e50c4c20-88bb-43b3-87a9-aab952916a76\",\n" +
                                    "        \"employee_name\": \"Scotty Frami DDS\",\n" +
                                    "        \"employee_salary\": 381603,\n" +
                                    "        \"employee_age\": 63,\n" +
                                    "        \"employee_title\": \"Administration Facilitator\",\n" +
                                    "        \"employee_email\": \"tickleme_pink@company.com\"\n" +
                                    "    },\n" +
                                    "    \"status\": \"Successfully processed request.\"\n" +
                                    "}"
                        )
                )
        );

        UUID id = UUID.fromString("e50c4c20-88bb-43b3-87a9-aab952916a76");
        EmployeeApiResponse employeeApiResponse = employeeApiClient.findById(id);

        Assertions.assertThat(employeeApiResponse)
                .isEqualTo(
                        new EmployeeApiResponse(
                                UUID.fromString("e50c4c20-88bb-43b3-87a9-aab952916a76"),
                                "Scotty Frami DDS",
                                381603,
                                63,
                                "Administration Facilitator",
                                "tickleme_pink@company.com")
                );
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenEmployeeNotPresent() {
        mockServer.when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api/v1/employee/e50c4c20-88bb-43b3-87a9-aab952916a70")
        ).respond(HttpResponse.response()
                .withStatusCode(404)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                            "{\n" +
                                    "    \"status\": \"Successfully processed request.\"\n" +
                                    "}"
                        )
                )
        );

        UUID id = UUID.fromString("e50c4c20-88bb-43b3-87a9-aab952916a70");

       Assertions.assertThatThrownBy(() -> employeeApiClient.findById(id))
               .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void shouldDeleteEmployeeByName() {
        mockServer.when(HttpRequest.request()
                .withMethod("DELETE")
                .withContentType(MediaType.APPLICATION_JSON)
                .withPath("/api/v1/employee")
                .withBody(JsonBody.json("{ \"name\" : \"Test Employee\"\n }"))
        ).respond(HttpResponse.response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(JsonBody.json(
                                "{\n" +
                                        "    \"data\": true,\n" +
                                        "    \"status\": \"Successfully processed request.\"\n" +
                                        "}"
                        )
                )
        );

        DeleteEmployeeApiRequest request = new DeleteEmployeeApiRequest("Test Employee");
        boolean deleted = employeeApiClient.deleteEmployee(request);
        Assertions.assertThat(deleted).isTrue();
    }

}