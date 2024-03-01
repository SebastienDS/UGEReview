package fr.uge.revue.service;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.TestsReview;
import fr.uge.revue.model.User;
import fr.uge.revue.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TestRunnerService testRunnerService;

    public ReviewService(ReviewRepository reviewRepository, TestRunnerService testRunnerService) {
        this.reviewRepository = Objects.requireNonNull(reviewRepository);
        this.testRunnerService = Objects.requireNonNull(testRunnerService);
    }

    public List<Review> allReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> searchReview(String search) {
        return reviewRepository.searchReview(Objects.requireNonNull(search));
    }

    public Optional<Review> getReview(long reviewID) { return reviewRepository.findByIdWithFullContent(reviewID); }

    @Transactional
    public Review createReview(CreateReviewDTO createReviewDTO, User user) {
        var review = new Review(createReviewDTO.title(), createReviewDTO.commentary(), createReviewDTO.code(), createReviewDTO.test(), user);
        review.setRequestNotifications(Set.of(user));
        try {
            var result = testRunnerService.launchTests(createReviewDTO.code(), createReviewDTO.test());
            review.setTests(new TestsReview(review, result.summary().getTestsSucceededCount(), result.summary().getTestsFoundCount()));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        reviewRepository.save(review);
        return review;
    }

    @Transactional
    public boolean delete(long id) {
        var review = getReview(id);
        if(review.isEmpty()){
            return false;
        }
        reviewRepository.delete(review.get());
        return true;
    }
}
