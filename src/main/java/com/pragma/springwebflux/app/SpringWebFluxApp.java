package com.pragma.springwebflux.app;

import com.pragma.springwebflux.app.dao.ProductRepository;
import com.pragma.springwebflux.app.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringWebFluxApp implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SpringWebFluxApp.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxApp.class, args);
    }

    @Override
    public void run(String... args) {

        mongoTemplate.dropCollection("products").subscribe();

        Flux
                .just(
                        new Product("TV Panasonic Pantalla LCD", 456.89),
                        new Product("Sony Camara HD Digital", 177.89),
                        new Product("Apple iPod", 46.89),
                        new Product("Sony Notebook", 846.89),
                        new Product("Hewlett Packard Multifuncional", 200.89),
                        new Product("Bianchi Bicicleta", 70.89),
                        new Product("HP Notebook Omen 17", 2500.89),
                        new Product("Mica Cómoda 5 Cajones", 150.89),
                        new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.89))
                .flatMap(product -> {
                    product.setCreateAt(new Date());
                    return productRepository.save(product);
                })
                .subscribe(product -> LOG.info(String.format("Insert: %s",product)));
    }
}
