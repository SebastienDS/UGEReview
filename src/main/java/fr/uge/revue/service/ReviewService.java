package fr.uge.revue.service;

import fr.uge.revue.model.Comment;
import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository, NotificationService notificationService) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    public List<Review> allReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> searchReview(String search) {
        return reviewRepository.searchReview(Objects.requireNonNull(search));
    }

    public Optional<Review> getReview(long reviewID) { return reviewRepository.findByIdWithFullContent(reviewID); }

    public void addComment(Review review, Comment comment) {
        review.addComment(comment);
        reviewRepository.save(review);
    }

    public Review createReview(CreateReviewDTO createReviewDTO, User user) {
        var review = new Review(createReviewDTO.title(), createReviewDTO.commentary(), createReviewDTO.code(), createReviewDTO.test(), user);
        review.setRequestNotifications(Set.of(user));
        reviewRepository.save(review);
        return review;
    }
}
