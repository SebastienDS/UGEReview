package fr.uge.revue;

import fr.uge.revue.model.Test;
import fr.uge.revue.repository.TestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner test(TestRepository repository) {
        return args -> {
            repository.save(new Test());
            System.out.println("repository.findAll() = " + repository.findAll());
            System.out.println("repository.findById(0) = " + repository.findById(0));
            System.out.println("repository.findById(1) = " + repository.findById(1));
            System.out.println("repository.findById(2) = " + repository.findById(2));
        };
    }
}