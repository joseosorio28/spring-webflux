package com.pragma.springwebflux.app.controller;

import com.pragma.springwebflux.app.dao.ProductRepository;
import com.pragma.springwebflux.app.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Controller
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
    private static final String TITLE = "title";
    private static final String PRODUCTS = "products";
    private static final String LIST_PRODUCTS = "List of products";
    private static final String FORMAT = "Product: %s";

    @Autowired
    private ProductRepository productRepository;

    @GetMapping({"/list", "/"})
    public String list(Model model) {
        Flux<Product> products = productRepository.findAll()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                });

        products.subscribe(product->LOG.info(String.format(FORMAT,product.getName())));
        model.addAttribute(PRODUCTS, products);
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list";

    }

    @GetMapping({"/list-datadriver"})
    public String listDataDriver(Model model) {
        Flux<Product> products = productRepository.findAll()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                })
                .delayElements(Duration.ofSeconds(1));

        products.subscribe(product->LOG.info(String.format(FORMAT,product.getName())));

        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products,2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list";

    }

    @GetMapping({"/full-list"})
    public String fullList(Model model) {
        Flux<Product> products = productRepository.findAll()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                })
                .repeat(5000);

        products.subscribe(product->LOG.info(String.format(FORMAT,product.getName())));

        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products,2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list";

    }

    @GetMapping({"/full-chunked"})
    public String fullListChunked(Model model) {
        Flux<Product> products = productRepository.findAll()
                .map(product->{
                    product.setName(product.getName().toUpperCase());
                    return product;
                })
                .repeat(5000);

        products.subscribe(product->LOG.info(String.format(FORMAT,product.getName())));

        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products,2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list-chunked";

    }

}
