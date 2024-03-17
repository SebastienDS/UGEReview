package fr.uge.revue.repository;

import fr.uge.revue.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByIdWithFullContent_ReturnsReviewWithFullContent() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);

        var review = new Review("review", "commentary", "code", "test", user1);
        var comment = new Comment("comment", user2, review);
        var response = new Response("response", user2, comment);
        user1.setReviews(Set.of(review));
        review.getComments().add(comment);
        comment.addResponse(response);

        userRepository.save(user1);
        userRepository.save(user2);

        var foundReviewOptional = reviewRepository.findByIdWithFullContent(review.getId());

        assertTrue(foundReviewOptional.isPresent());
        Review foundReview = foundReviewOptional.get();
        assertEquals(user1, foundReview.getAuthor());
        assertNotNull(foundReview.getComments());
        assertEquals(user2, foundReview.getComments().stream().findFirst().get().getAuthor());
        assertNotNull(foundReview.getComments().stream().findFirst().get().getResponses());
        assertEquals(user2, foundReview.getComments().stream().findFirst().get().getResponses().stream().findFirst().get().getAuthor());
    }

    @Test
    public void findAll_ReturnsAllReviews() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);

        var review1 = new Review("review1", "commentary", "code", "test", user1);
        var review2 = new Review("review2", "commentary", "code", "test", user1);

        userRepository.save(user1);
        userRepository.save(user2);
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        var reviews = reviewRepository.findAll();
        assertEquals(2, reviews.size());
        assertNotNull(reviews.get(0).getAuthor());
    }

    @Test
    public void searchReview_ReturnsSearchedReviews() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user);
        review1.setDate(new Date(0));
        var review2 = new Review("review2", "commentary", "code", "test", user);
        review2.setDate(new Date(10000));
        var review3 = new Review("review3", "commentary", "code", "test", user);
        review3.setDate(new Date(20000));

        userRepository.save(user);
        reviewRepository.saveAll(List.of(review1, review2, review3));

        var reviews1 = reviewRepository.searchReview("", PageRequest.of(0, 5));
        assertEquals(3, reviews1.getContent().size());

        var reviews2 = reviewRepository.searchReview("review1", PageRequest.of(0, 5));
        assertEquals(1, reviews2.getContent().size());
        assertEquals(review1, reviews2.getContent().get(0));

        var reviews3 = reviewRepository.searchReview("review", PageRequest.of(0, 5));
        assertEquals(3, reviews3.getContent().size());

        var reviews4 = reviewRepository.searchReview("review", PageRequest.of(0, 1));
        assertEquals(1, reviews4.getContent().size());
        assertEquals(review3, reviews4.getContent().get(0));

        var reviews5 = reviewRepository.searchReview("review", PageRequest.of(2, 1));
        assertEquals(1, reviews5.getContent().size());
        assertEquals(review1, reviews5.getContent().get(0));
    }

    @Test
    public void findAllUserReviews_ReturnsUserReviews() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user1);
        review1.setDate(new Date(0));
        var review2 = new Review("review2", "commentary", "code", "test", user1);
        review2.setDate(new Date(10000));
        var review3 = new Review("review3", "commentary", "code", "test", user2);
        review3.setDate(new Date(20000));

        userRepository.saveAll(List.of(user1, user2));
        reviewRepository.saveAll(List.of(review1, review2, review3));

        var reviews1 = reviewRepository.findAllUserReviews(user1.getId(), PageRequest.of(0, 5));
        assertEquals(2, reviews1.getContent().size());

        var reviews2 = reviewRepository.findAllUserReviews(user2.getId(), PageRequest.of(0, 5));
        assertEquals(1, reviews2.getContent().size());

        var reviews3 = reviewRepository.findAllUserReviews(user1.getId(), PageRequest.of(0, 1));
        assertEquals(1, reviews3.getContent().size());
        assertEquals(review2, reviews3.getContent().get(0));

        var reviews4 = reviewRepository.findAllUserReviews(user1.getId(), PageRequest.of(1, 1));
        assertEquals(1, reviews4.getContent().size());
        assertEquals(review1, reviews4.getContent().get(0));

        var reviews5 = reviewRepository.findAllUserReviews(user1.getId(), PageRequest.of(2, 1));
        assertEquals(0, reviews5.getContent().size());
    }

    @Test
    public void findAllUserReviewsMatching_ReturnsMatchingUserReviews() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);

        var review1 = new Review("review1", "commentary", "code", "test", user1);
        var review2 = new Review("review2", "commentary", "code", "test", user1);

        userRepository.save(user1);
        userRepository.save(user2);
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        var reviews1 = reviewRepository.findAllUserReviewsMatching(user1.getId(), "review1");
        assertEquals(1, reviews1.size());
        assertEquals(review1, reviews1.get(0));

        var reviews2 = reviewRepository.findAllUserReviewsMatching(user1.getId(), "review2");
        assertEquals(1, reviews2.size());
        assertEquals(review2, reviews2.get(0));

        var reviews = reviewRepository.findAllUserReviewsMatching(user1.getId(), "");
        assertEquals(2, reviews.size());
    }

    @Test
    public void findByIdWithNotifications_ReturnsReviewWithNotifications() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        review.getRequestNotifications().add(user);

        userRepository.save(user);
        reviewRepository.save(review);

        var reviewFound = reviewRepository.findByIdWithNotifications(review.getId());
        assertTrue(reviewFound.isPresent());
        assertNotNull(reviewFound.get().getRequestNotifications());
        assertEquals(1, reviewFound.get().getRequestNotifications().size());
        assertEquals(user, reviewFound.get().getRequestNotifications().stream().findFirst().get());
    }

    @Test
    public void findReviewPage_ReturnsReviewPage() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user1);
        review1.setDate(new Date(0));
        var review2 = new Review("review2", "commentary", "code", "test", user1);
        review2.setDate(new Date(10000));
        var review3 = new Review("review3", "commentary", "code", "test", user2);
        review3.setDate(new Date(20000));

        userRepository.saveAll(List.of(user1, user2));
        reviewRepository.saveAll(List.of(review1, review2, review3));

        var reviews1 = reviewRepository.findReviewPage(PageRequest.of(0, 5));
        assertEquals(3, reviews1.getContent().size());

        var reviews2 = reviewRepository.findReviewPage(PageRequest.of(0, 1));
        assertEquals(1, reviews2.getContent().size());
        assertEquals(review3, reviews2.getContent().get(0));

        var reviews3 = reviewRepository.findReviewPage(PageRequest.of(1, 1));
        assertEquals(1, reviews3.getContent().size());
        assertEquals(review2, reviews3.getContent().get(0));

        var reviews4 = reviewRepository.findReviewPage(PageRequest.of(2, 1));
        assertEquals(1, reviews4.getContent().size());
        assertEquals(review1, reviews4.getContent().get(0));
    }

    @Test
    public void findUsersPageReviewsOrderDesc_ReturnsUsersPageReviewsInDescendingOrder() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        User user3 = new User("testuser2", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user1);
        review1.setDate(new Date(0));
        var review2 = new Review("review2", "commentary", "code", "test", user1);
        review2.setDate(new Date(10000));
        var review3 = new Review("review3", "commentary", "code", "test", user2);
        review3.setDate(new Date(20000));
        var review4 = new Review("review4", "commentary", "code", "test", user3);
        review4.setDate(new Date(30000));

        userRepository.saveAll(List.of(user1, user2, user3));
        reviewRepository.saveAll(List.of(review1, review2, review3, review4));

        var reviews1 = reviewRepository.findUsersPageReviewsOrderDesc(List.of(user1.getId(), user2.getId()), PageRequest.of(0, 5));
        assertEquals(3, reviews1.getContent().size());

        var reviews2 = reviewRepository.findUsersPageReviewsOrderDesc(List.of(user3.getId()), PageRequest.of(0, 5));
        assertEquals(1, reviews2.getContent().size());

        var reviews3 = reviewRepository.findUsersPageReviewsOrderDesc(List.of(user1.getId(), user2.getId()), PageRequest.of(0, 1));
        assertEquals(1, reviews3.getContent().size());
        assertEquals(review3, reviews3.getContent().get(0));

        var reviews4 = reviewRepository.findUsersPageReviewsOrderDesc(List.of(user1.getId(), user2.getId()), PageRequest.of(1, 1));
        assertEquals(1, reviews4.getContent().size());
        assertEquals(review2, reviews4.getContent().get(0));

        var reviews5 = reviewRepository.findUsersPageReviewsOrderDesc(List.of(user1.getId(), user2.getId()), PageRequest.of(2, 1));
        assertEquals(1, reviews5.getContent().size());
        assertEquals(review1, reviews5.getContent().get(0));
    }

    @Test
    public void findReviewPageWithoutUserIds_ReturnsReviewPageWithoutSpecifiedUserIds() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        User user3 = new User("testuser2", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user1);
        review1.setDate(new Date(0));
        var review2 = new Review("review2", "commentary", "code", "test", user1);
        review2.setDate(new Date(10000));
        var review3 = new Review("review3", "commentary", "code", "test", user2);
        review3.setDate(new Date(20000));
        var review4 = new Review("review4", "commentary", "code", "test", user3);
        review4.setDate(new Date(30000));

        userRepository.saveAll(List.of(user1, user2, user3));
        reviewRepository.saveAll(List.of(review1, review2, review3, review4));

        var reviews1 = reviewRepository.findReviewPageWithoutUserIds(List.of(user1.getId(), user2.getId()), PageRequest.of(0, 5));
        assertEquals(1, reviews1.size());

        var reviews2 = reviewRepository.findReviewPageWithoutUserIds(List.of(user3.getId()), PageRequest.of(0, 5));
        assertEquals(3, reviews2.size());

        var reviews3 = reviewRepository.findReviewPageWithoutUserIds(List.of(user3.getId()), PageRequest.of(0, 1));
        assertEquals(1, reviews3.size());
        assertEquals(review3, reviews3.get(0));

        var reviews4 = reviewRepository.findReviewPageWithoutUserIds(List.of(user3.getId()), PageRequest.of(1, 1));
        assertEquals(1, reviews4.size());
        assertEquals(review2, reviews4.get(0));

        var reviews5 = reviewRepository.findReviewPageWithoutUserIds(List.of(user3.getId()), PageRequest.of(2, 1));
        assertEquals(1, reviews5.size());
        assertEquals(review1, reviews5.get(0));
    }

    @Test
    public void findAllReviewsWhereUserIsRequestingNotifications_ReturnsReviewsWhereUserIsRequestingNotifications() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review1 = new Review("review1", "commentary", "code", "test", user);
        var review2 = new Review("review2", "commentary", "code", "test", user);
        var review3 = new Review("review2", "commentary", "code", "test", user);
        review1.getRequestNotifications().add(user);
        review2.getRequestNotifications().add(user);

        userRepository.save(user);
        reviewRepository.saveAll(List.of(review1, review2, review3));

        var reviews = reviewRepository.findAllReviewsWhereUserIsRequestingNotifications(user);
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertFalse(reviews.contains(review3));
    }
}