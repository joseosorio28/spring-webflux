package com.pragma.springwebflux.app.service;

import com.pragma.springwebflux.app.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

    Flux<Product> findAll();
    Flux<Product> findAllWithNameUppercase();
    Mono<Product> findById(String id);
    Mono<Product> save(Product product);
    Mono<Void> delete(Product product);

}
