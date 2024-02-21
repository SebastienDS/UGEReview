package fr.uge.revue.controller;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.User;
import fr.uge.revue.service.CommentService;
import fr.uge.revue.service.ReviewService;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final CommentService commentService;

    public ReviewController(ReviewService reviewService, UserService userService, CommentService commentService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public RedirectView goToReviews() {
        return new RedirectView("/reviews");
    }

    @GetMapping("/reviews")
    public String allReviews(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authenticated", true);
        }
        var reviews = reviewService.allReviews().stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/reviews")
    public String searchReview(@ModelAttribute("search") String search, Model model) {
        var reviews = reviewService.searchReview(search).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @GetMapping("/reviews/{reviewId}")
    public String oneReview(@PathVariable long reviewId, Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authenticated", true);
        }
        var review = reviewService.getReview(reviewId);
        if(review.isEmpty()){
            return "notFound";
        }
        model.addAttribute("review", ReviewOneReviewDTO.from(review.get()));
        model.addAttribute("reviewId", reviewId);
        return "review";
    }

    @PostMapping("/reviews/{reviewId}/like")
    public RedirectView toggleReviewLikeButton(@PathVariable long reviewId, Model model, Authentication authentication) {
        var review = reviewService.getReview(reviewId);
        var userId = ((User) authentication.getPrincipal()).getId();
        review.ifPresent(value -> {
            userService.toggleLikeReview(userId, value);
        });
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/reviews/{reviewId}/dislike")
    public RedirectView toggleReviewDisLikeButton(@PathVariable long reviewId, Model model, Authentication authentication) {
        var review = reviewService.getReview(reviewId);
        var userId = ((User) authentication.getPrincipal()).getId();
        review.ifPresent(value -> {
            userService.toggleDislikeReview(userId, value);
        });
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/comments/{commentId}/likeComment")
    public RedirectView toggleCommentLikeButton(@PathVariable long commentId, Model model, Authentication authentication) {
        var comment = commentService.getComment(commentId);
        var userId = ((User) authentication.getPrincipal()).getId();
        comment.ifPresent(value -> {
            userService.toggleLikeComment(userId, value);
        });
        return new RedirectView("/comments/" + commentId);
    }

    @PostMapping("/comments/{commentId}/dislikeComment")
    public RedirectView toggleCommentDisLikeButton(@PathVariable long commentId, Model model, Authentication authentication) {
        var comment = commentService.getComment(commentId);
        var userId = ((User) authentication.getPrincipal()).getId();
        comment.ifPresent(value -> {
            userService.toggleLikeComment(userId, value);
        });
        return new RedirectView("/comments/" + commentId);
    }

    @GetMapping("/createReview")
    public String getCreateReviewForm() {
        return "/createReview";
    }

    @PostMapping("/createReview")
    public RedirectView createReview(Model model, Authentication authentication, @ModelAttribute CreateReviewDTO createReviewDTO) {
        var user = (User) authentication.getPrincipal();
        var review = userService.createReview(createReviewDTO, user);
        return new RedirectView("/reviews/" + review.getId());
    }

    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<String> createComment(Model model, @PathVariable long reviewId, @RequestBody String content, Authentication authentication, @ModelAttribute CreateReviewDTO createReviewDTO) {
        Objects.requireNonNull(content);
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not connected");
        }
        var user = (User) authentication.getPrincipal();
        var review = reviewService.getReview(reviewId);
        if(user == null){
            System.out.println("cc");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not connected");
        }
        if(review.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review Not Found");
        }
        var comment = new Comment(content, user, review.get());
        commentService.saveComment(comment);
        reviewService.addComment(review.get(), comment);
        userService.addComment(user.getId(), comment);
        return ResponseEntity.ok("");
    }

}
