package fr.uge.revue;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.repository.CommentRepository;
import fr.uge.revue.repository.ResponseRepository;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import fr.uge.revue.service.CommentService;
import fr.uge.revue.service.ResponseService;
import fr.uge.revue.service.ReviewService;
import fr.uge.revue.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "ENVIRONMENT", havingValue = "PRODUCTION")
    public CommandLineRunner test(ReviewService reviewService, UserRepository userRepository,
                                  CommentService commentService, ResponseService responseService, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            var userDeleted = new User("USER_DELETED", "", "", Role.USER);
            var userBanned = new User("USER_BANNED", "", "", Role.USER);
            userDeleted.setId(1L);
            userBanned.setId(2L);
            userRepository.save(userDeleted);
            userRepository.save(userBanned);
            var user = new User("User1", "user1@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user);
            var user2 = new User("User2", "user2@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user2);
            var user3 = new User("User3", "user3@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user3);
            var user4 = new User("User4", "user4@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user4);
            var user5 = new User("User5", "user5@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user5);
            var user6 = new User("User6", "user6@gmail.com", passwordEncoder.encode("user1"), Role.USER);
            userRepository.save(user6);
            user.setFollowers(new HashSet<>(Arrays.asList(user2, user3, user4)));
            user4.setFollowers(new HashSet<>(List.of(user5)));
            userRepository.save(user);
            userRepository.save(user4);
            var arnaud = new User("arnaud", "arnaud@gmail.com", passwordEncoder.encode("arnaud"), Role.USER);
            userRepository.save(arnaud);
            var users = List.of(user, user2, user3, user4, user5, user6, arnaud);
            var comments = List.of("UwU",
                    """ 
                    ```
                    public int add(int a, int b) {
                        return a + b;
                    }
                    ```
                    Je pense pas que tu es besoin de faire une fonction pour faire une addition...
                    Pense a utilisé directement le plus. (Ara Ara~)
                    """,
                    """ 
                   ```
                   public void failTest() {
                        ClassToTest instance = new ClassToTest();
                        assertEquals(5, instance.add(2, 2));
                    }
                   }
                   ```
                   Je n'arrive pas à comprendre pourquoi le test fonctionne pas. Peut etre parce que 2 hommes + 2 femmes = infinité d'enfant donc infinité != 5?
                   """);
            var responses = List.of("Ara Ara~",
                    """ 
                    ```
                    public int SLYWIN(int SLY, int Random) {
                        return SLY;
                    }
                    ```
                    Peut etre ajouté que les randoms perde forcément en print ?
                    """,
                    """ 
                   ```
                   Vive League Of Legends (Séb est aigri)
                   ```
                   """);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < users.size(); j++) {
                    var review = reviewService.createReview(new CreateReviewDTO("Review" + i + "-" + j, "Commentaire",
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
                                    """, null, null), users.get(j));
                    for (int k = 0; k < 10; k++) {
                        var comment = new Comment(comments.get(ThreadLocalRandom.current().nextInt(comments.size())), users.get(ThreadLocalRandom.current().nextInt(users.size())), review);
                        commentService.saveComment(comment);
                        for (int l = 0; l < ThreadLocalRandom.current().nextInt(5); l++) {
                            responseService.saveResponse(new Response(responses.get(ThreadLocalRandom.current().nextInt(responses.size())),
                                    users.get(ThreadLocalRandom.current().nextInt(users.size())),
                                    comment));
                        }
                    }

                }
            }
        };
    }
}