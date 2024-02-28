package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class ReviewRestControler {

    private final ReviewService reviewService;

    public ReviewRestControler(ReviewService reviewService) {
        this.reviewService = Objects.requireNonNull(reviewService);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewOneReviewDTO> oneReview(@PathVariable long reviewId) {
        var review = reviewService.getReview(reviewId);
        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ReviewOneReviewDTO.from(review.get()));
    }
}
