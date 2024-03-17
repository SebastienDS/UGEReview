package fr.uge.revue.service;

import fr.uge.revue.model.*;
import fr.uge.revue.repository.ResponseRepository;
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
class ResponseServiceTest {
    private ResponseService responseService;
    @Mock
    private ResponseRepository responseRepository;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        responseService = new ResponseService(responseRepository, notificationService);
    }

    @Test
    void getResponse() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.getReviews().add(review);
        review.getComments().add(comment);
        comment.addResponse(response);

        given(responseRepository.findByIdWithComment(response.getId()))
                .willReturn(Optional.of(response));

        var responseFound = responseService.getResponse(response.getId());
        assertTrue(responseFound.isPresent());
        assertEquals(response, responseFound.get());
        assertEquals(comment, responseFound.get().getComment());
    }

    @Test
    void getResponse_notFound() {
        given(responseRepository.findByIdWithComment(5))
                .willReturn(Optional.empty());

        var commentFound = responseService.getResponse(5);
        assertTrue(commentFound.isEmpty());
    }

    @Test
    void saveResponse() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        var review = new Review("review", "commentary", "code", "test", user);
        var comment = new Comment("comment", user, review);
        var response = new Response("response", user, comment);
        user.getReviews().add(review);
        review.getComments().add(comment);
        comment.addResponse(response);

        responseService.saveResponse(response);

        verify(responseRepository).save(response);
        verify(notificationService).notifyNewResponse(response);
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

        given(responseRepository.findByIdWithComment(response.getId()))
                .willReturn(Optional.of(response));

        var res = responseService.delete(response.getId());
        assertTrue(res);
        verify(responseRepository).delete(response);
    }

    @Test
    void delete_notFound() {
        given(responseRepository.findByIdWithComment(5))
                .willReturn(Optional.empty());

        var res = responseService.delete(5);
        assertFalse(res);
        verify(responseRepository, never()).delete(any());
    }
}