package com.reliaquest.api.client.inbound;

import com.reliaquest.api.controller.inbound.CreateEmployeeRequest;

public record CreateEmployeeApiRequest(
        String name,
        Integer salary,
        Integer age,
        String title){
    public static CreateEmployeeApiRequest from(CreateEmployeeRequest createEmployeeRequest) {
        return new CreateEmployeeApiRequest(
                createEmployeeRequest.name(),
                createEmployeeRequest.salary(),
                createEmployeeRequest.age(),
                createEmployeeRequest.title()
        );
    }
}
