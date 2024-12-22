package com.reliaquest.api.model;

import java.util.UUID;

public record Employee(
        UUID id,
        String name,
        Integer salary,
        Integer age,
        String title,
        String email
) { }
