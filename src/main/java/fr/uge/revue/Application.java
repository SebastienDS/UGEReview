package fr.uge.revue;

import fr.uge.revue.model.Review;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
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
    public CommandLineRunner test(UserRepository userRepository, ReviewRepository reviewRepository) {
        return args -> {
            var user = new User("User1", "user1@gmail.com", "user1password", Role.USER);
            userRepository.save(user);

            var review = new Review("Review1", "code", "test", user);
            reviewRepository.save(review);
            var review2 = new Review("Review2", "code", "test", user);
            reviewRepository.save(review2);
            var review3 = new Review("Rom le bg mysterieux", "code", "test", user);
            reviewRepository.save(review3);
            var review4 = new Review("Quentin le brigand", "code", "test", user);
            reviewRepository.save(review4);
        };
    }
}