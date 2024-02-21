package fr.uge.revue;

import fr.uge.revue.model.*;
import fr.uge.revue.repository.CommentRepository;
import fr.uge.revue.repository.ResponseRepository;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner test(UserRepository userRepository, ReviewRepository reviewRepository, CommentRepository commentRepository, ResponseRepository responseRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            var userDeleted = new User("UserDeleted", "", "", Role.USER);
            userDeleted.setId(1L);
            userRepository.save(userDeleted);
            var user = new User("User1", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user);

            var review = new Review("Review1", "commentary", "code", "test", user);
            reviewRepository.save(review);
            var review2 = new Review("Review2", "commentary","code", "test", user);
            reviewRepository.save(review2);
            var review3 = new Review("Rom le bg mysterieux", "commentary","code", "test", user);
            reviewRepository.save(review3);
            var review4 = new Review("Quentin le brigand", "commentary","code", "test", user);
            reviewRepository.save(review4);

            var user2 = new User("test", "test@gmail.com", passwordEncoder.encode("test"), Role.USER);
            userRepository.save(user2);
            userRepository.save(new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), Role.ADMIN));
            reviewRepository.findAll().forEach(System.out::println);

            var comment = new Comment("Kakukaku", user, review);
            var comment2 = new Comment("Kukakuka", user2, review);
            var comment3 = new Comment("Quentin est nul", user, review2);
            commentRepository.saveAll(List.of(comment, comment2, comment3));
            for (int i = 0; i < 100; i++) {
                commentRepository.save(new Comment("test", user, review));
            }

            responseRepository.save(new Response("response1", user, comment));
            responseRepository.save(new Response("response2", user2, comment));
            responseRepository.save(new Response("response3", user, comment2));
        };
    }
}