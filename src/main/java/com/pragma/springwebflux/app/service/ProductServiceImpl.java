package com.pragma.springwebflux.app.service;

import com.pragma.springwebflux.app.controller.ProductController;
import com.pragma.springwebflux.app.dao.ProductRepository;
import com.pragma.springwebflux.app.model.Product;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Flux<Product> findAllWithNameUppercase() {
        return productRepository.findAll()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                });
    }

    @Override
    public Flux<Product> findAllWithNameUppercaseRepeat() {
        return findAllWithNameUppercase().repeat(5000);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findAll()
                .filter(product -> product.getId().equals(id))
                .next()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                })
                .doOnNext(product -> LOG.info(product.getName()));
    }

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productRepository.delete(product);
    }
}
