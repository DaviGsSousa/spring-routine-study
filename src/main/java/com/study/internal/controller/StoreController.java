package com.study.internal.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Store;
import com.study.internal.service.StoreService;
import com.study.internal.utils.TableOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name="StoreController", description = "REST de Store")
@RestController
@Secured("ROLE_USER")
@RequestMapping("/store")
public class StoreController {

    final StoreService storeService;
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @JsonView(Store.SimpleView.class)
    public Page<Store> list(TableOptions tableOptions) {
        return this.storeService.list(tableOptions);
    }

    @GetMapping("/filter")
    @JsonView(Store.SimpleView.class)
    public Page<Store> filter(TableOptions tableOptions) {
        return this.storeService.list(tableOptions);
    }

    @PostMapping
    @JsonView(Store.ExtendedView.class)
    public Store register(@RequestBody Store Store) {
        return this.storeService.register(Store);
    }

    @PutMapping("/{storeId}")
    @JsonView(Store.ExtendedView.class)
    public Store update(@PathVariable("storeId") Long id, @RequestBody JsonNode t) {
        return this.storeService.update(id, t);
    }

    @DeleteMapping("/{storeId}")
    public void delete(@PathVariable("storeId") Long id) {
        this.storeService.delete(id);
    }

}
