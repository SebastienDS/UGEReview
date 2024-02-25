package fr.uge.revue.service;

import fr.uge.revue.model.*;
import fr.uge.revue.repository.NotificationRepository;
import fr.uge.revue.repository.ReviewRepository;
import fr.uge.revue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.notificationRepository = Objects.requireNonNull(notificationRepository);
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public void notifyNewComment(Comment comment) {
        Objects.requireNonNull(comment);
        var review = reviewRepository.findByIdWithNotifications(comment.getReview().getId()).orElseThrow();
        notifyOtherUsers(review, comment.getAuthor(), user -> Notification.newComment(user, review.getId(), comment.getId()));
    }

    public void notifyNewResponse(Response response) {
        Objects.requireNonNull(response);
        var review = reviewRepository.findByIdWithNotifications(response.getComment().getReview().getId()).orElseThrow();
        notifyOtherUsers(review, response.getAuthor(), user -> Notification.newResponse(user, review.getId(), response.getId()));
    }

    private void notifyOtherUsers(Review review, User author, Function<User, Notification> notificationMapper) {
        review.getRequestNotifications()
                .stream()
                .filter(user -> !author.equals(user))
                .map(notificationMapper)
                .forEach(notificationRepository::save);
    }

    public boolean isUserRequestingNotification(long reviewId, long userId) {
        return notificationRepository.isUserRequestingNotification(reviewId, userId);
    }

    public void activateNotifications(long reviewId, User user) {
        Objects.requireNonNull(user);
        var review = reviewRepository.findByIdWithNotifications(reviewId).orElseThrow();
        review.getRequestNotifications().add(user);
        reviewRepository.save(review);
    }

    public void deactivateNotifications(long reviewId, User user) {
        Objects.requireNonNull(user);
        var review = reviewRepository.findByIdWithNotifications(reviewId).orElseThrow();
        review.getRequestNotifications().remove(user);
        reviewRepository.save(review);
    }

    public Set<Notification> findAllUserNotifications(long userId) {
        return notificationRepository.findAllUserNotifications(userId);
    }
}
