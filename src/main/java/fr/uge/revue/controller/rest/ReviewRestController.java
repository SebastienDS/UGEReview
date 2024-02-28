package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.review.ReviewCreatedDTO;
import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.model.User;
import fr.uge.revue.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ReviewRestController {
    private final ReviewService reviewService;

    public ReviewRestController(ReviewService reviewService) {
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

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewAllReviewDTO>> allReviews(@RequestParam Optional<String> search) {
        var reviews = search.map(reviewService::searchReview)
                .orElseGet(reviewService::allReviews)
                .stream().map(ReviewAllReviewDTO::from).toList();
        return ResponseEntity.ok().body(reviews);
    }

    @PostMapping("/createReview")
    public ResponseEntity<ReviewCreatedDTO> createReview(@RequestBody CreateReviewDTO createReviewDTO, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var review = reviewService.createReview(createReviewDTO, user);
        return ResponseEntity.ok().body(new ReviewCreatedDTO(review.getId()));
    }
}
