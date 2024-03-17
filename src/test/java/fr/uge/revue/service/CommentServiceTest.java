package fr.uge.revue.service;

import fr.uge.revue.model.*;
import fr.uge.revue.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        commentService = new CommentService(commentRepository, notificationService);
    }

    @Test
    void getComment() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        user.getReviews().add(review);
        review.getComments().add(comment);

        given(commentRepository.findByIdWithReview(comment.getId()))
                .willReturn(Optional.of(comment));

        var commentFound = commentService.getComment(comment.getId());
        assertTrue(commentFound.isPresent());
        assertEquals(comment, commentFound.get());
        assertEquals(review, commentFound.get().getReview());
    }

    @Test
    void getComment_notFound() {
        given(commentRepository.findByIdWithReview(5))
                .willReturn(Optional.empty());

        var commentFound = commentService.getComment(5);
        assertTrue(commentFound.isEmpty());
    }

    @Test
    void saveComment() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        user.getReviews().add(review);
        review.getComments().add(comment);

        commentService.saveComment(comment);

        verify(commentRepository).save(comment);
        verify(notificationService).notifyNewComment(comment);
    }

    @Test
    void getCommentWithResponse() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.getReviews().add(review);
        review.getComments().add(comment);
        comment.addResponse(response);

        given(commentRepository.findByIdWithResponses(comment.getId()))
                .willReturn(Optional.of(comment));

        var commentFound = commentService.getCommentWithResponse(comment.getId());
        assertTrue(commentFound.isPresent());
        assertEquals(comment, commentFound.get());
        assertNotNull(commentFound.get().getResponses());
        assertEquals(1, commentFound.get().getResponses().size());
        assertEquals(response, commentFound.get().getResponses().stream().findFirst().get());
    }

    @Test
    void getCommentWithResponse_notFound() {
        given(commentRepository.findByIdWithResponses(5))
                .willReturn(Optional.empty());

        var commentFound = commentService.getCommentWithResponse(5);
        assertTrue(commentFound.isEmpty());
    }

    @Test
    void delete() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.getReviews().add(review);
        review.getComments().add(comment);
        comment.addResponse(response);

        given(commentRepository.findByIdWithReview(comment.getId()))
                .willReturn(Optional.of(comment));

        var res = commentService.delete(comment.getId());
        assertTrue(res);
        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_notFound() {
        given(commentRepository.findByIdWithReview(5))
                .willReturn(Optional.empty());

        var res = commentService.delete(5);
        assertFalse(res);
        verify(commentRepository, never()).delete(any());
    }
}