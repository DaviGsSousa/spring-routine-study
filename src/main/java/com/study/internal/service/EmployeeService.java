package com.study.internal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Employee;
import com.study.internal.repository.EmployeeRepository;
import com.study.internal.utils.TableOptions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    final EmployeeRepository employeeRepository;
    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Page<Employee> list(TableOptions tableOptions){
        return this.employeeRepository.findAll(tableOptions.toPageable());
    }

    public Page<Employee> filter(TableOptions tableOptions){
        return this.employeeRepository.findAll(tableOptions.toSpecification(), tableOptions.toPageable());
    }

    public Employee register(Employee t){
        return this.employeeRepository.save(t);
    }

    public Employee update(Long id, JsonNode t){
        return this.employeeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(("There's no Employees with id: " + id + " found."))).update(t);
    }

    public void delete(Long id) {
        this.employeeRepository.deleteById(id);
    }
}
