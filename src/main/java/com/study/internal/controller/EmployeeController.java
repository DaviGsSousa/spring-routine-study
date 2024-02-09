package com.study.internal.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Employee;
import com.study.internal.service.EmployeeService;
import com.study.internal.utils.TableOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name="EmployeeController", description = "REST de Employee")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @JsonView(Employee.SimpleView.class)
    public Page<Employee> list(TableOptions tableOptions) {
        return this.employeeService.list(tableOptions);
    }

    @GetMapping("/filter")
    @JsonView(Employee.SimpleView.class)
    public Page<Employee> filter(TableOptions tableOptions) {
        return this.employeeService.list(tableOptions);
    }

    @PostMapping
    @JsonView(Employee.ExtendedView.class)
    public Employee register(@RequestBody Employee Employee) {
        return this.employeeService.register(Employee);
    }

    @PutMapping("/{employeeId}")
    @JsonView(Employee.ExtendedView.class)
    public Employee update(@PathVariable("employeeId") Long id, @RequestBody JsonNode t) {
        return this.employeeService.update(id, t);
    }

    @DeleteMapping("/{employeeId}")
    public void delete(@PathVariable("employeeId") Long id) {
        this.employeeService.delete(id);
    }

}
