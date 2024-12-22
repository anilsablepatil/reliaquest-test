package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmployeeServiceConfig {

    @Bean
    public WebClient employeeServiceWebClient(
            @Value("${EMPLOYEE_SERVICE_BASE_PATH}") String employeeServiceBasePath) {
        return WebClient.create(employeeServiceBasePath);
    }
}
