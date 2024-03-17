package fr.uge.revue.repository;

import fr.uge.revue.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ResponseRepositoryTest {

    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void findByIdWithComment_ReturnsResponseWithComment() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.setReviews(Set.of(review));
        review.getComments().add(comment);
        comment.addResponse(response);

        userRepository.save(user);
        reviewRepository.save(review);

        var foundResponseOptional = responseRepository.findByIdWithComment(response.getId());

        assertTrue(foundResponseOptional.isPresent());
        var foundResponse = foundResponseOptional.get();
        assertEquals(comment, foundResponse.getComment());
    }

    @Test
    public void findByAuthorIdPage_ReturnsResponsesByAuthorPage() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);

        var review1 = new Review("review", "commentary", "code", "test", user1);
        var comment1 = new Comment("comment", user1, review1);
        var response1 = new Response("response", user1, comment1);
        user1.setReviews(Set.of(review1));
        review1.getComments().add(comment1);
        comment1.addResponse(response1);

        var review2 = new Review("review", "commentary", "code", "test", user2);
        var comment2 = new Comment("comment", user2, review2);
        var response2 = new Response("response", user2, comment2);
        user2.setReviews(Set.of(review2));
        review2.getComments().add(comment2);
        comment2.addResponse(response2);

        userRepository.saveAll(List.of(user1, user2));
        reviewRepository.saveAll(List.of(review1, review2));

        var responsePage = responseRepository.findByAuthorIdPage(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());

        var responsePage2 = responseRepository.findByAuthorIdPage(user2.getId(), PageRequest.of(0, 10));
        assertNotNull(responsePage2);
        assertEquals(1, responsePage2.getTotalElements());
    }
}