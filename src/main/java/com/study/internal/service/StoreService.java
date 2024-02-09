package com.study.internal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.study.internal.entity.Store;
import com.study.internal.repository.StoreRepository;
import com.study.internal.utils.TableOptions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class StoreService {

    final StoreRepository storeRepository;
    public StoreService(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    public Page<Store> list(TableOptions tableOptions){
        return this.storeRepository.findAll(tableOptions.toPageable());
    }

    public Page<Store> filter(TableOptions tableOptions){
        return this.storeRepository.findAll(tableOptions.toSpecification(), tableOptions.toPageable());
    }

    public Store register(Store t){
        return this.storeRepository.save(t);
    }

    public Store update(Long id, JsonNode t){
        return this.storeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(("There's no Employees with id: " + id + " found."))).update(t);
    }

    public void delete(Long id) {
        this.storeRepository.deleteById(id);
    }

}
