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
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void findByIdWithReview_ReturnsCommentWithReview() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.setReviews(Set.of(review));
        review.getComments().add(comment);
        comment.addResponse(response);

        userRepository.save(user);
        reviewRepository.save(review);

        var foundCommentOptional = commentRepository.findByIdWithReview(comment.getId());

        assertTrue(foundCommentOptional.isPresent());
        assertEquals(comment, foundCommentOptional.get());
        assertEquals(review, foundCommentOptional.get().getReview());
    }

    @Test
    public void findByIdWithResponses_ReturnsCommentWithResponses() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.setReviews(Set.of(review));
        review.getComments().add(comment);
        comment.addResponse(response);

        userRepository.save(user);
        reviewRepository.save(review);

        var foundCommentOptional = commentRepository.findByIdWithReview(comment.getId());

        assertTrue(foundCommentOptional.isPresent());
        assertEquals(comment, foundCommentOptional.get());
        assertTrue(foundCommentOptional.get().getResponses().contains(response));
    }

    @Test
    public void findByAuthorId_ReturnsCommentsByAuthor() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user);
        var comment1 = new Comment("comment", user, review1);
        var response1 = new Response("response", user, comment1);
        review1.getComments().add(comment1);
        comment1.addResponse(response1);

        var review2 = new Review("review2", "commentary", "code", "test", user);
        var comment2 = new Comment("comment", user, review2);
        var response2 = new Response("response", user, comment2);
        review2.getComments().add(comment2);
        comment2.addResponse(response2);

        user.getReviews().add(review1);
        user.getReviews().add(review2);

        userRepository.save(user);
        reviewRepository.saveAll(List.of(review1, review2));

        var page = commentRepository.findByAuthorId(user.getId(), PageRequest.of(0, 5));
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
    }
}