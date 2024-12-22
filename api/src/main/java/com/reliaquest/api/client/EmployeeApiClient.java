package com.reliaquest.api.client;

import com.reliaquest.api.client.inbound.CreateEmployeeApiRequest;
import com.reliaquest.api.client.inbound.DeleteEmployeeApiRequest;
import com.reliaquest.api.client.outbound.EmployeeApiResponse;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class EmployeeApiClient {
    private WebClient employeeServiceWebClient;

    public EmployeeApiClient(@Autowired WebClient employeeServiceWebClient) {
        this.employeeServiceWebClient = employeeServiceWebClient;
    }

    public EmployeeApiResponse createEmployee(CreateEmployeeApiRequest createEmployeeApiRequest) {
        CreateEmployeeResponse createEmployeeResponse = employeeServiceWebClient
                .post()
                .uri("/api/v1/employee")
                .bodyValue(createEmployeeApiRequest)
                .retrieve()
                .bodyToMono(CreateEmployeeResponse.class)
                .block();
        return createEmployeeResponse.data();
    }

    public List<EmployeeApiResponse> findAllEmployee() {
        EmployeeListResponse employeeListResponse = employeeServiceWebClient
                .get()
                .uri("/api/v1/employee")
                .retrieve()
                .bodyToMono(EmployeeListResponse.class)
                .block();
        return employeeListResponse.data();
    }

    public EmployeeApiResponse findById(UUID id) {
        GetEmployeeByIdResponse employeeByIdResponse = employeeServiceWebClient
                .get()
                .uri("/api/v1/employee/" + id)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() == 404, response -> response.bodyToMono(String.class)
                        .flatMap( body -> {
                            throw new EmployeeNotFoundException("Employee with id " + id + " Not found.");
                        })
                )
                .bodyToMono(GetEmployeeByIdResponse.class)
                .block();
        return employeeByIdResponse.data();
    }

    public boolean deleteEmployee(DeleteEmployeeApiRequest request) {
        DeleteEmployeeByNameResponse deleteByNameResponse = employeeServiceWebClient
                .method(HttpMethod.DELETE)
                .uri("/api/v1/employee")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DeleteEmployeeByNameResponse.class)
                .block();
        return deleteByNameResponse.data();
    }


}

record EmployeeListResponse(
        List<EmployeeApiResponse> data,
        String status
) { }

record CreateEmployeeResponse(
        EmployeeApiResponse data,
        String status
) { }

record GetEmployeeByIdResponse(
        EmployeeApiResponse data,
        String status
) { }

record DeleteEmployeeByNameResponse(
    boolean data,
    String status
) { }

