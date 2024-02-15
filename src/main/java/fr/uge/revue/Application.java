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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner test(UserRepository userRepository, ReviewRepository reviewRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            var userDeleted = new User("UserDeleted", "", "", Role.USER);
            userDeleted.setId(1L);
            userRepository.save(userDeleted);
            var user = new User("User1", "user1@gmail.com", passwordEncoder.encode("user1password"), Role.USER);
            userRepository.save(user);

            var review = new Review("Review1", "commentary", "code", "test", user);
            reviewRepository.save(review);
            var review2 = new Review("Review2", "commentary","code", "test", user);
            reviewRepository.save(review2);
            var review3 = new Review("Rom le bg mysterieux", "commentary","code", "test", user);
            reviewRepository.save(review3);
            var review4 = new Review("Quentin le brigand", "commentary","code", "test", user);
            reviewRepository.save(review4);

            userRepository.save(new User("test", "test@gmail.com", passwordEncoder.encode("test"), Role.USER));
            userRepository.save(new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), Role.ADMIN));
            reviewRepository.findAll().forEach(System.out::println);
        };
    }
}