package fr.uge.revue.service;

import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
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
}
