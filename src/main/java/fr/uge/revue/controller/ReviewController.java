package fr.uge.revue.controller;

import fr.uge.revue.dto.review.ReviewDTO;
import fr.uge.revue.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public String allReviews(Model model) {
        var reviews = reviewService.allReviews().stream().map(ReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }
}
