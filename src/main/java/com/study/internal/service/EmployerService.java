package com.study.internal.service;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Employer;
import com.study.internal.entity.Product;
import com.study.internal.repository.EmployerRepository;
import com.study.internal.utils.TableOptions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.NoSuchElementException;

@Service
public class EmployerService {

    final EmployerRepository employerRepository;
    public EmployerService(EmployerRepository employerRepository){
        this.employerRepository = employerRepository;
    }

    public Page<Employer> list(TableOptions tableOptions){
        return this.employerRepository.findAll(tableOptions.toPageable());
    }

    public Page<Employer> filter(TableOptions tableOptions){
        return this.employerRepository.findAll(tableOptions.toSpecification(), tableOptions.toPageable());
    }

    public Employer register(Employer t){
        return this.employerRepository.save(t);
    }

    public Employer update(Long id, JsonNode t){
        return this.employerRepository.findById(id).orElseThrow(() -> new NoSuchElementException(("There's no Employers with id: " + id + " found."))).update(t);
    }

    public void delete(Long id) {
        this.employerRepository.deleteById(id);
    }
}
