package com.reliaquest.api.client.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reliaquest.api.model.Employee;

import java.util.UUID;

public record EmployeeApiResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("employee_name") String name,
        @JsonProperty("employee_salary") Integer salary,
        @JsonProperty("employee_age") Integer age,
        @JsonProperty("employee_title") String title,
        @JsonProperty("employee_email") String email
) {

    public Employee toEmployee() {
        return new Employee(
                id, name, salary, age, title, email
        );
    }
}
