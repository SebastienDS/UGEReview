package fr.uge.revue.controller;

import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.User;
import fr.uge.revue.service.CommentService;
import fr.uge.revue.service.ResponseService;
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
    private final ResponseService responseService;

    public ReviewController(ReviewService reviewService, UserService userService, CommentService commentService, ResponseService responseService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.commentService = commentService;
        this.responseService = responseService;
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
        var review = reviewService.getReview(reviewId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleLikeReview(userId, review);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/reviews/{reviewId}/dislike")
    public RedirectView toggleReviewDisLikeButton(@PathVariable long reviewId, Model model, Authentication authentication) {
        var review = reviewService.getReview(reviewId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleDislikeReview(userId, review);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/comments/{commentId}/like")
    public RedirectView toggleCommentLikeButton(@PathVariable long commentId, Model model, Authentication authentication) {
        var comment = commentService.getComment(commentId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleLikeComment(userId, comment);
        return new RedirectView("/reviews/" + comment.getReview().getId() + "#comment_" + commentId);
    }

    @PostMapping("/comments/{commentId}/dislike")
    public RedirectView toggleCommentDisLikeButton(@PathVariable long commentId, Model model, Authentication authentication) {
        var comment = commentService.getComment(commentId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleDislikeComment(userId, comment);
        return new RedirectView("/reviews/" + comment.getReview().getId() + "#comment_" + commentId);
    }

    @PostMapping("/responses/{responseId}/like")
    public RedirectView toggleResponseLikeButton(@PathVariable long responseId, Model model, Authentication authentication) {
        var response = responseService.getResponse(responseId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleLikeResponse(userId, response);
        return new RedirectView("/reviews/" + response.getComment().getReview().getId() + "#response_" + responseId);
    }

    @PostMapping("/responses/{responseId}/dislike")
    public RedirectView toggleResponseDisLikeButton(@PathVariable long responseId, Model model, Authentication authentication) {
        var response = responseService.getResponse(responseId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        userService.toggleDislikeResponse(userId, response);
        return new RedirectView("/reviews/" + response.getComment().getReview().getId() + "#response_" + responseId);
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
