package fr.uge.revue.controller;

import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String allReviews(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("connected", true);
        }
        var reviews = reviewService.allReviews().stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/")
    public String searchReview(@ModelAttribute("search") String search, Model model) {
        var reviews = reviewService.searchReview(search).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @GetMapping("/reviews/{reviewID}")
    public String oneReviews(@PathVariable("reviewID")long reviewID, Model model) {
        var review = reviewService.getReview(reviewID);
        if(review.isEmpty()){
            return "notFound";
        }
        model.addAttribute("reviews", ReviewOneReviewDTO.from(review.get()));
        return "review";
    }
}
