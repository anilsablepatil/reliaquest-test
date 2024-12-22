package com.reliaquest.api.controller;


import com.reliaquest.api.ApiApplication;
import com.reliaquest.api.client.EmployeeApiClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "EMPLOYEE_SERVICE_BASE_PATH=http://localhost:6868")
public class EmployeeControllerFullIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    private static ClientAndServer mockServer;

    @Autowired
    private EmployeeApiClient employeeApiClient;

    @BeforeAll
    static public void setupMockServer() {
        mockServer = new ClientAndServer(6868);
        setupData();
    }

    @AfterAll
    static public void destroyMockServer() {
        mockServer.stop();
    }

    @Test
    public void shouldFindAllEmployee() {
        webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].name").isEqualTo("Scotty Frami DDS");
    }

    @Test
    public void shouldSearchEmployee() {
        webTestClient
                .get()
                .uri("/search/Russell DuBuque V")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].id").isEqualTo("ad0cb745-1c7a-4682-a99a-8bcaa6793c35");
    }

    @Test
    public void shouldGetEmployeeById() {
        webTestClient
                .get()
                .uri("/e50c4c20-88bb-43b3-87a9-aab952916a76")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("Scotty Frami DDS");
    }

    @Test
    public void shouldGetHighestSalary() {
        webTestClient
                .get()
                .uri("/highestSalary")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isEqualTo("381603");
    }

    @Test
    public void shouldDeleteEmployeeById() {
        webTestClient
                .delete()
                .uri("/e50c4c20-88bb-43b3-87a9-aab952916a76")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void shouldThrow404WhenDeleteEmployeeByIdNotPresent() {
        webTestClient
                .delete()
                .uri("/f50c4c20-88bb-43b3-87a9-aab952916a76")
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(404));
    }

    @Test
    public void shouldCreateEmployee() {
        webTestClient
                .post()
                .uri("/")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"name\": \"Test Employee\",\n" +
                        "    \"salary\": 10000,\n" +
                        "    \"age\": 22,\n" +
                        "    \"title\": \"Test\"\n" +
                        "}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(201))
                .expectBody().jsonPath("$.id").isEqualTo("788b9bd5-4c81-470d-b52a-50e443509283");
    }

    public static void setupData() {
        // create employee
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

        // get all employee
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


        // employee get by id
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

        // employee not found
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

        // delete by name
        mockServer.when(HttpRequest.request()
                .withMethod("DELETE")
                .withContentType(MediaType.APPLICATION_JSON)
                .withPath("/api/v1/employee")
                .withBody(JsonBody.json("{ \"name\" : \"Scotty Frami DDS\"\n }"))
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
    }

}