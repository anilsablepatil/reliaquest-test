package com.reliaquest.api.controller.inbound;

public record CreateEmployeeRequest(
        String name,
        Integer salary,
        Integer age,
        String title){ }
