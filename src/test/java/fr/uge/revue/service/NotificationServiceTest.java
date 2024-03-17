package fr.uge.revue.service;

import fr.uge.revue.model.*;
import fr.uge.revue.repository.NotificationRepository;
import fr.uge.revue.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {
        notificationService = new NotificationService(notificationRepository, reviewRepository);
    }


    @Test
    void notifyNewComment() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(1);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);
        var review = new Review("review", "commentary", "code", "test", user1);
        review.setId(3);
        var comment = new Comment("comment", user1, review);
        comment.setId(3);

        review.getComments().add(comment);
        review.getRequestNotifications().add(user2);

        given(reviewRepository.findByIdWithNotifications(comment.getReview().getId()))
                .willReturn(Optional.of(review));

        notificationService.notifyNewComment(comment);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(user1, captor.getValue().getUserWhoNotify());
        assertEquals(user2, captor.getValue().getNotifiedUser());
    }

    @Test
    void notifyNewComment_dontNotifyMyself() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(1);
        var review = new Review("review", "commentary", "code", "test", user1);
        review.setId(3);
        var comment = new Comment("comment", user1, review);
        comment.setId(3);

        review.getComments().add(comment);
        review.getRequestNotifications().add(user1);

        given(reviewRepository.findByIdWithNotifications(comment.getReview().getId()))
                .willReturn(Optional.of(review));

        notificationService.notifyNewComment(comment);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, never()).save(captor.capture());
    }

    @Test
    void notifyNewResponse() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(1);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(2);
        var review = new Review("review", "commentary", "code", "test", user1);
        review.setId(3);
        var comment = new Comment("comment", user1, review);
        comment.setId(3);
        var response = new Response("response", user1, comment);
        comment.setId(3);

        review.getComments().add(comment);
        comment.addResponse(response);
        review.getRequestNotifications().add(user2);

        given(reviewRepository.findByIdWithNotifications(response.getComment().getReview().getId()))
                .willReturn(Optional.of(review));

        notificationService.notifyNewResponse(response);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(user1, captor.getValue().getUserWhoNotify());
        assertEquals(user2, captor.getValue().getNotifiedUser());
    }

    @Test
    void notifyNewResponse_dontNotifyMyself() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(1);
        var review = new Review("review", "commentary", "code", "test", user1);
        review.setId(3);
        var comment = new Comment("comment", user1, review);
        comment.setId(3);
        var response = new Response("response", user1, comment);
        comment.setId(3);

        review.getComments().add(comment);
        comment.addResponse(response);
        review.getRequestNotifications().add(user1);

        given(reviewRepository.findByIdWithNotifications(response.getComment().getReview().getId()))
                .willReturn(Optional.of(review));

        notificationService.notifyNewResponse(response);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, never()).save(captor.capture());
    }

    @Test
    void isUserRequestingNotification() {
        var reviewId = 0;
        var userId = 1;
        notificationService.isUserRequestingNotification(reviewId, userId);

        verify(notificationRepository).isUserRequestingNotification(reviewId, userId);
    }

    @Test
    void activateNotifications() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        user.setId(1);
        var review = new Review("review", "commentary", "code", "test", user);
        review.setId(2);

        assertFalse(review.getRequestNotifications().contains(user));

        given(reviewRepository.findByIdWithNotifications(review.getId()))
                .willReturn(Optional.of(review));

        notificationService.activateNotifications(review.getId(), user);

        var captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertTrue(captor.getValue().getRequestNotifications().contains(user));
    }

    @Test
    void deactivateNotifications() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        user.setId(1);
        var review = new Review("review", "commentary", "code", "test", user);
        review.setId(2);
        review.getRequestNotifications().add(user);

        assertTrue(review.getRequestNotifications().contains(user));

        given(reviewRepository.findByIdWithNotifications(review.getId()))
                .willReturn(Optional.of(review));

        notificationService.deactivateNotifications(review.getId(), user);

        var captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertFalse(captor.getValue().getRequestNotifications().contains(user));
    }

    @Test
    void findAllUserNotifications() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(0);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(1);

        var n1 = Notification.newComment(user1, user2, 0, 0);
        n1.setId(1);
        var n2 = Notification.newComment(user1, user2, 0, 0);
        n2.setId(2);
        var n3 = Notification.newComment(user1, user2, 0, 0);
        n3.setId(3);
        n3.setAlreadyRead(true);

        given(notificationRepository.findAllUserNotifications(user1.getId()))
                .willReturn(Set.of(n1, n2, n3));

        var userNotifications = notificationService.findAllUserNotifications(user1.getId());

        assertEquals(2, userNotifications.size());
        userNotifications.forEach(notification -> assertEquals(user1, notification.getNotifiedUser()));
    }

    @Test
    void markAsRead() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(0);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(1);

        var n1 = Notification.newComment(user1, user2, 0, 0);
        n1.setId(1);

        given(notificationRepository.findByIdWithNotifiedUser(n1.getId()))
                .willReturn(Optional.of(n1));

        assertFalse(n1.isAlreadyRead());

        var res = notificationService.markAsRead(user1, n1.getId());
        assertTrue(res);

        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertTrue(captor.getValue().isAlreadyRead());
    }

    @Test
    void markAsRead_notFoundShouldReturnFalse() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(0);

        given(notificationRepository.findByIdWithNotifiedUser(5))
                .willReturn(Optional.empty());

        var res = notificationService.markAsRead(user1, 5);
        assertFalse(res);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void markAsRead_returnFalseIfItNotMyNotification() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(0);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(1);

        var n1 = Notification.newComment(user2, user1, 0, 0);
        n1.setId(1);

        given(notificationRepository.findByIdWithNotifiedUser(n1.getId()))
                .willReturn(Optional.of(n1));

        var res = notificationService.markAsRead(user1, n1.getId());
        assertFalse(res);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void findById() {
        User user1 = new User("testuser1", "test@example.com", "password", Role.USER);
        user1.setId(0);
        User user2 = new User("testuser2", "test@example.com", "password", Role.USER);
        user2.setId(1);

        var n1 = Notification.newComment(user1, user2, 0, 0);
        n1.setId(1);

        given(notificationRepository.findById(0L))
                .willReturn(Optional.of(n1));

        var notificationFound = notificationService.findById(0);
        assertTrue(notificationFound.isPresent());
        assertEquals(n1, notificationFound.get());
    }
}