package fr.uge.revue.controller;

import fr.uge.revue.dto.review.ReviewDTO;
import fr.uge.revue.model.Review;
import fr.uge.revue.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

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

    @PostMapping("/reviews")
    public String searchReview(@ModelAttribute("search") String search, Model model) {
        var reviews = reviewService.searchReview(search).stream().map(ReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

}
