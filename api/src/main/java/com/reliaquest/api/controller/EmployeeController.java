package com.reliaquest.api.controller;

import com.reliaquest.api.controller.inbound.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController()
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private EmployeeService employeeService;

    public EmployeeController(@Autowired EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> listOfEmployees = employeeService.getAllEmployees();
        return ResponseEntity.ok(listOfEmployees);
    }

    @GetMapping("/search/{searchString}")
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> listOfEmployees = employeeService.findEmployeeByName(searchString);
        return ResponseEntity.ok(listOfEmployees);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/highestSalary")
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.findHighestSalaryOfEmployee();
        return ResponseEntity.ok(highestSalary);
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> names = employeeService.findTopTenEarningEmployeeNames();
        return ResponseEntity.ok(names);
    }

    @PostMapping("/")
    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        Employee savedEmployee = employeeService.saveEmployee(createEmployeeRequest);
        return new ResponseEntity(savedEmployee, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        employeeService.deleteById(UUID.fromString(id));
        return ResponseEntity.ok("Employee Deleted Successfully");
    }
}
