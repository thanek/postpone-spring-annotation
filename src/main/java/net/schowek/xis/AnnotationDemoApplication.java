package net.schowek.xis;

import net.schowek.xis.example.storage.MongoInvocationRepository;
import net.schowek.xis.spring.postpones.EnablePostpones;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePostpones(repository = MongoInvocationRepository.class)
public class AnnotationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationDemoApplication.class, args);
    }
}
