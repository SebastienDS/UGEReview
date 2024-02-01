package fr.uge.revue.service;

import fr.uge.revue.model.Review;
import fr.uge.revue.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> allReviews() {
        return reviewRepository.findAll();
    }
}
