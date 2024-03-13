package fr.uge.revue;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.repository.CommentRepository;
import fr.uge.revue.repository.ResponseRepository;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import fr.uge.revue.service.ReviewService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner test(ReviewService reviewService, UserRepository userRepository, ReviewRepository reviewRepository, CommentRepository commentRepository, ResponseRepository responseRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            var userDeleted = new User("UserDeleted", "", "", Role.USER);
            var userBanned = new User("UserBanned", "", "", Role.USER);
            userDeleted.setId(1L);
            userBanned.setId(2L);
            userRepository.save(userDeleted);
            userRepository.save(userBanned);
            var user = new User("User1", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user);
            var user2 = new User("User2", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user2);
            var user3 = new User("User3", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user3);
            var user4 = new User("User4", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user4);
            var user5 = new User("User5", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user5);
            var user6 = new User("User6", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user6);
            user.setFollowers(new HashSet<>(Arrays.asList(user2, user3, user4)));
            user4.setFollowers(new HashSet<>(List.of(user5)));
            userRepository.save(user);
            userRepository.save(user4);



            var review = new Review("Review1", "commentary", "code", "test", user);
            review.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review);
            var review2 = new Review("Review2", "commentary","code", "test", user);
            review2.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review2);
            var review3 = new Review("Rom le bg mysterieux", "commentary","code", "test", user);
            review3.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review3);
            var review4 = new Review("Quentin le brigand", "commentary","code", "test", user);
            review4.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review4);
            var review5 = new Review("Review1a", "commentary", "code", "test", user2);
            review5.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review5);
            var review6 = new Review("Review1b", "commentary", "code", "test", user3);
            review6.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review6);
            var review7 = new Review("Review1c", "commentary", "code", "test", user6);
            review7.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review7);
            var review8 = new Review("Review1d", "commentary", "code", "test", user6);
            review8.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review8);
            var review9 = new Review("Review1e", "commentary", "code", "test", user5);
            review9.setUnitTests(new TestsReview(0, 0));
            reviewRepository.save(review9);

            var userTest = new User("test", "test@gmail.com", passwordEncoder.encode("test"), Role.USER);
            userRepository.save(userTest);
            userRepository.save(new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), Role.ADMIN));
            reviewRepository.findAll().forEach(System.out::println);

            var comment = new Comment("Kakukaku", user, review);
            var comment2 = new Comment("Kukakuka", userTest, review);
            var comment3 = new Comment("UwU", user, review2);
            commentRepository.saveAll(List.of(comment, comment2, comment3));
            for (int i = 0; i < 100; i++) {
                commentRepository.save(new Comment("test", user, review));
            }

            responseRepository.save(new Response("response1", user, comment));
            responseRepository.save(new Response("response2", userTest, comment));
            responseRepository.save(new Response("response3", user, comment2));

            reviewService.createReview(new CreateReviewDTO("Review", "Commentaire",
                        """
                        public class ClassToTest {
                            public int add(int a, int b) {
                                return a + b;
                            }
                        }
                        """,
                        """
                        import org.junit.jupiter.api.Test;
                        import static org.junit.jupiter.api.Assertions.*;
                        
                        public class DynamicTest {
                            @Test
                            public void testAddition() {
                                ClassToTest instance = new ClassToTest();
                                assertEquals(4, instance.add(2, 2));
                            }
                            
                            @Test
                            public void failTest() {
                                ClassToTest instance = new ClassToTest();
                                assertEquals(5, instance.add(2, 2));
                            }
                        }
                        """, null, null), user2);
        };
    }
}