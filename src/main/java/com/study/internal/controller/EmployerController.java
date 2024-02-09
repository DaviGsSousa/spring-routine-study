package com.study.internal.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Employer;
import com.study.internal.service.EmployerService;
import com.study.internal.utils.TableOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name="EmployerController", description = "REST de Employer")
@RestController
@RequestMapping("/employer")
public class EmployerController {

    final EmployerService employerService;
    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @GetMapping
    @JsonView(Employer.SimpleView.class)
    public Page<Employer> list(TableOptions tableOptions) {
        return this.employerService.list(tableOptions);
    }

    @GetMapping("/filter")
    @JsonView(Employer.SimpleView.class)
    public Page<Employer> filter(TableOptions tableOptions) {
        return this.employerService.list(tableOptions);
    }

    @PostMapping
    @JsonView(Employer.ExtendedView.class)
    public Employer register(@RequestBody Employer Employer) {
        return this.employerService.register(Employer);
    }

    @PutMapping("/{employerId}")
    @JsonView(Employer.ExtendedView.class)
    public Employer update(@PathVariable("employerId") Long id, @RequestBody JsonNode t) {
        return this.employerService.update(id, t);
    }

    @DeleteMapping("/{employerId}")
    public void delete(@PathVariable("employerId") Long id) {
        this.employerService.delete(id);
    }

}
