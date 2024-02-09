package com.study.internal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Product;
import com.study.internal.repository.ProductRepository;
import com.study.internal.utils.TableOptions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Page<Product> list(TableOptions tableOptions){
        return this.productRepository.findAll(tableOptions.toPageable());
    }

    public Page<Product> filter(TableOptions tableOptions){
        return this.productRepository.findAll(tableOptions.toSpecification(), tableOptions.toPageable());
    }

    public Product register(Product t){
        return this.productRepository.save(t);
    }

    public Product update(Long id, JsonNode t){
        return this.productRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There's no Products found with id: " + id)).update(t);
    }

    public void delete(Long id){
        this.productRepository.deleteById(id);
    }
}
