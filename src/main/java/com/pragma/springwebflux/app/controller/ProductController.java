package com.pragma.springwebflux.app.controller;

import com.pragma.springwebflux.app.model.Product;
import com.pragma.springwebflux.app.service.IProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;

@Controller
@AllArgsConstructor
@SessionAttributes("product")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
    private static final String TITLE = "title";
    private static final String PRODUCTS = "products";
    private static final String BUTTON = "button";

    private static final String LIST_PRODUCTS = "List of products";
    private static final String FORMAT = "Product: %s";

    //@Autowired
    private IProductService productService;

    @GetMapping({"/list", "/"})
    public Mono<String> list(Model model) {
        Flux<Product> products = productService.findAllWithNameUppercase();
        products.subscribe(product -> LOG.info(String.format(FORMAT, product.getName())));
        model.addAttribute(PRODUCTS, products);
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return Mono.just("list");
    }

    @GetMapping("/form")
    public Mono<String> create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute(TITLE, "Product form");
        model.addAttribute(BUTTON, "Create");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(
            @Valid Product product,
            BindingResult result,
            Model model,
            SessionStatus sessionStatus) {

        if (result.hasErrors()) {
            model.addAttribute(TITLE, "ERROR");
            model.addAttribute(BUTTON, "Create");
            return Mono.just("form");
        } else {
            sessionStatus.setComplete();

            if (product.getCreateAt() == null) {
                product.setCreateAt(new Date());
            }

            return productService.save(product)
                    .doOnNext(p -> LOG.info("Product saved: " + p.getName() + "Id:" + p.getId()))
                    .thenReturn("redirect:/list?success=saved+product")
                    ;
        }
    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(
            @PathVariable String id,
            Model model) {
        return productService.findById(id)
                .doOnNext(p -> {
                    model.addAttribute(TITLE, "Edit p");
                    model.addAttribute("product", p);
                    model.addAttribute(BUTTON, "Edit");
                    LOG.info("Product saved: " + p.getName() + ", Id:" + p.getId());
                })
                .defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("Does not exist"));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/list?error=Product+doesn't+exist"));
    }

    @GetMapping("/delete/{id}")
    public Mono<String> delete(
            @PathVariable String id,
            Model model) {
        return productService.findById(id)
                .defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("Does not exist"));
                    }
                    return Mono.just(p);
                }).
                flatMap(p -> {
                    return productService.delete(p);
                }).
                then(Mono.just("redirect:/list?success=deleted+product"))
                .onErrorResume(ex -> Mono.just("redirect:/list?error=Product+doesn't+exist"));
    }

    @GetMapping({"/list-datadriver"})
    public String listDataDriver(Model model) {
        Flux<Product> products = productService.findAllWithNameUppercase()
                .delayElements(Duration.ofSeconds(1));
        products.subscribe(product -> LOG.info(String.format(FORMAT, product.getName())));
        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list";

    }

    @GetMapping({"/full-list"})
    public String fullList(Model model) {
        Flux<Product> products = productService.findAllWithNameUppercaseRepeat();
        products.subscribe(product -> LOG.info(String.format(FORMAT, product.getName())));
        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list";
    }

    @GetMapping({"/full-chunked"})
    public String fullListChunked(Model model) {
        Flux<Product> products = productService.findAllWithNameUppercaseRepeat();
        products.subscribe(product -> LOG.info(String.format(FORMAT, product.getName())));
        model.addAttribute(PRODUCTS, new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute(TITLE, LIST_PRODUCTS);
        return "list-chunked";

    }

}
