package fr.uge.revue.repository;


import fr.uge.revue.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_ReturnsCorrectUser() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        userRepository.save(user);

        var userFound = userRepository.findByUsername("testuser");

        assertTrue(userFound.isPresent());
        assertEquals("testuser", userFound.get().getUsername());
    }

    @Test
    public void findByEmail_ReturnsCorrectUser() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        userRepository.save(user);

        var userFound = userRepository.findByEmail("test@example.com");

        assertTrue(userFound.isPresent());
        assertEquals("test@example.com", userFound.get().getEmail());
    }


    @Test
    public void findByIdWithContent_ReturnsUserWithContent() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithContent(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getReviews());
        assertNotNull(foundUser.get().getComments());
        assertNotNull(foundUser.get().getResponses());
        assertEquals(1, foundUser.get().getReviews().size());
        assertEquals(1, foundUser.get().getComments().size());
        assertEquals(1, foundUser.get().getResponses().size());
    }

    @Test
    public void findByIdWithReviewLikes_ReturnsUserWithReviewLikesAndNoDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setReviewsLikes(Set.of(review));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getReviewsLikes());
        assertNotNull(foundUser.get().getReviewsDislikes());

        assertEquals(1, foundUser.get().getReviewsLikes().size());
        assertEquals(0, foundUser.get().getReviewsDislikes().size());

        assertTrue(foundUser.get().getReviewsLikes().contains(review));
    }

    @Test
    public void findByIdWithReviewLikes_ReturnsUserWithReviewNoLikesAndDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setReviewsDislikes(Set.of(review));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getReviewsLikes());
        assertNotNull(foundUser.get().getReviewsDislikes());

        assertEquals(0, foundUser.get().getReviewsLikes().size());
        assertEquals(1, foundUser.get().getReviewsDislikes().size());

        assertTrue(foundUser.get().getReviewsDislikes().contains(review));
    }

    @Test
    public void findByIdWithCommentLikes_ReturnsUserWithCommentLikesAndNoDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setCommentsLikes(Set.of(comment));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getCommentsLikes());
        assertNotNull(foundUser.get().getCommentsDislikes());

        assertEquals(1, foundUser.get().getCommentsLikes().size());
        assertEquals(0, foundUser.get().getCommentsDislikes().size());

        assertTrue(foundUser.get().getCommentsLikes().contains(comment));
    }

    @Test
    public void findByIdWithCommentLikes_ReturnsUserWithCommentNoLikesAndDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setCommentsDislikes(Set.of(comment));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getCommentsLikes());
        assertNotNull(foundUser.get().getCommentsDislikes());

        assertEquals(0, foundUser.get().getCommentsLikes().size());
        assertEquals(1, foundUser.get().getCommentsDislikes().size());

        assertTrue(foundUser.get().getCommentsDislikes().contains(comment));
    }

    @Test
    public void findByIdWithResponseLikes_ReturnsUserWithResponseLikesAndNoDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setResponsesLikes(Set.of(response));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getResponsesLikes());
        assertNotNull(foundUser.get().getResponsesDislikes());

        assertEquals(1, foundUser.get().getResponsesLikes().size());
        assertEquals(0, foundUser.get().getResponsesDislikes().size());

        assertTrue(foundUser.get().getResponsesLikes().contains(response));
    }

    @Test
    public void findByIdWithResponseLikes_ReturnsUserWithResponseNoLikesAndDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.setReviews(Set.of(review));
        user.setComments(Set.of(comment));
        user.setResponses(Set.of(response));

        user.setResponsesDislikes(Set.of(response));
        userRepository.save(user);

        var foundUser = userRepository.findByIdWithReviewLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getResponsesLikes());
        assertNotNull(foundUser.get().getResponsesDislikes());

        assertEquals(0, foundUser.get().getResponsesLikes().size());
        assertEquals(1, foundUser.get().getResponsesDislikes().size());

        assertTrue(foundUser.get().getResponsesDislikes().contains(response));
    }

    @Test
    public void findByIdWithFollowers_ReturnsUserWithFollowers() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var follower1 = new User("follower1", "follower1@example.com", "password", Role.USER);
        var follower2 = new User("follower2", "follower2@example.com", "password", Role.USER);

        userRepository.save(follower1);
        userRepository.save(follower2);

        user.setFollowers(Set.of(follower1, follower2));
        userRepository.save(user);


        var foundUser = userRepository.findByIdWithFollowers(user.getId());

        assertTrue(foundUser.isPresent());
        var followers = foundUser.get().getFollowers();
        assertNotNull(followers);
        assertEquals(2, followers.size());
        assertTrue(followers.contains(follower1));
        assertTrue(followers.contains(follower2));
    }

    @Test
    public void findAllUsernames_ReturnsAllUsernames() {
        var user1 = new User("user1", "user1@example.com", "password", Role.USER);
        var user2 = new User("user2", "user2@example.com", "password", Role.USER);
        userRepository.save(user1);
        userRepository.save(user2);

        var usernames = userRepository.findAllUsernames();

        assertEquals(2, usernames.size());
        assertTrue(usernames.contains("user1"));
        assertTrue(usernames.contains("user2"));
    }

    @Test
    public void findAllEmails_ReturnsAllEmails() {
        var user1 = new User("testuser1", "test1@example.com", "password", Role.USER);
        var user2 = new User("testuser2", "test2@example.com", "password", Role.USER);
        userRepository.save(user1);
        userRepository.save(user2);

        var emails = userRepository.findAllEmails();

        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertTrue(emails.contains("test1@example.com"));
        assertTrue(emails.contains("test2@example.com"));
    }

    @Test
    public void findByIdWithLikes_ReturnsUserWithLikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.getReviews().add(review);
        user.getComments().add(comment);
        user.getResponses().add(response);

        user.getReviewsLikes().add(review);
        user.getCommentsLikes().add(comment);
        user.getResponsesLikes().add(response);

        userRepository.save(user);

        var foundUser = userRepository.findByIdWithLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getReviewsLikes());
        assertNotNull(foundUser.get().getCommentsLikes());
        assertNotNull(foundUser.get().getResponsesLikes());
    }

    @Test
    public void findByIdWithLikesAndDislikes_ReturnsUserWithLikesAndDislikes() {
        var user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);

        user.getReviews().add(review);
        user.getComments().add(comment);
        user.getResponses().add(response);

        user.getReviewsLikes().add(review);
        user.getCommentsDislikes().add(comment);

        userRepository.save(user);

        var foundUser = userRepository.findByIdWithLikes(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(1, foundUser.get().getReviewsLikes().size());
        assertEquals(0, foundUser.get().getReviewsDislikes().size());
        assertEquals(0, foundUser.get().getCommentsLikes().size());
        assertEquals(1, foundUser.get().getCommentsDislikes().size());
        assertEquals(0, foundUser.get().getResponsesLikes().size());
        assertEquals(0, foundUser.get().getResponsesDislikes().size());
    }
}