package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.client.inbound.CreateEmployeeApiRequest;
import com.reliaquest.api.client.inbound.DeleteEmployeeApiRequest;
import com.reliaquest.api.client.outbound.EmployeeApiResponse;
import com.reliaquest.api.controller.inbound.CreateEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private EmployeeApiClient employeeApiClient;

    public EmployeeService(@Autowired EmployeeApiClient employeeApiClient) {
        this.employeeApiClient = employeeApiClient;
    }

    public List<Employee> getAllEmployees() {
        return employeeApiClient.findAllEmployee()
                .stream()
                .map(EmployeeApiResponse::toEmployee)
                .collect(Collectors.toList());
    }

    public List<Employee> findEmployeeByName(String searchString) {
       return getAllEmployees()
               .stream()
               .filter(employee -> searchString != null &&  searchString.equals(employee.name()))
               .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        UUID employeeId = UUID.fromString(id);
        EmployeeApiResponse employeeApiResponse = employeeApiClient.findById(employeeId);
        return employeeApiResponse.toEmployee();
    }

    public Integer findHighestSalaryOfEmployee() {
        return getAllEmployees()
                .stream()
                .map(Employee::salary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> findTopTenEarningEmployeeNames() {
       return getAllEmployees()
               .stream()
               .sorted(Comparator.comparingInt(Employee::salary).reversed())
               .map(Employee::name)
               .limit(10)
               .collect(Collectors.toList());
    }

    public Employee saveEmployee(CreateEmployeeRequest createEmployeeRequest) {
        CreateEmployeeApiRequest apiRequest = CreateEmployeeApiRequest.from(createEmployeeRequest);
        EmployeeApiResponse apiResponse = employeeApiClient.createEmployee(apiRequest);
        return apiResponse.toEmployee();
    }

    public void deleteById(UUID idOfEmployeeToBeDeleated) {
        Optional<String> nameOfEmployee = getAllEmployees()
                .stream()
                .filter(employee -> employee.id().equals(idOfEmployeeToBeDeleated))
                .map(employee -> employee.name())
                .findFirst();

        if(nameOfEmployee.isPresent()) {
            DeleteEmployeeApiRequest deleteEmployeeApiRequest = new DeleteEmployeeApiRequest(nameOfEmployee.get());
            employeeApiClient.deleteEmployee(deleteEmployeeApiRequest);
        }
        else {
            throw new EmployeeNotFoundException("Employee with ID " + idOfEmployeeToBeDeleated + "Not Found");
        }
    }
}
