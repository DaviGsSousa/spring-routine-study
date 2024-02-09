package com.study.internal.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Product;
import com.study.internal.service.ProductService;
import com.study.internal.utils.TableOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name="ProductController", description = "REST de Product")
@RestController
@RequestMapping("/product")
public class ProductController {

    final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    @JsonView(Product.SimpleView.class)
    public Page<Product> list(TableOptions tableOptions){
        return this.productService.list(tableOptions);
    }

    @GetMapping("/filter")
    @JsonView(Product.SimpleView.class)
    public Page<Product> filter(TableOptions tableOptions){
        return this.productService.list(tableOptions);
    }

    @PostMapping
    @JsonView(Product.ExtendedView.class)
    public Product register(@RequestBody Product product){
        return this.productService.register(product);
    }

    @PutMapping("/{productId}")
    @JsonView(Product.ExtendedView.class)
    public Product update(@PathVariable("productId") Long id, @RequestBody JsonNode t){
        return this.productService.update(id, t);
    }

    @DeleteMapping("/{productId}")
    public void delete(@PathVariable("productId") Long id){
        this.productService.delete(id);
    }
}
