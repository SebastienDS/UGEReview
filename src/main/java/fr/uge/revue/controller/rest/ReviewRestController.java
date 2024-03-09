package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.comment.CommentDTO;
import fr.uge.revue.dto.response.ResponseDTO;
import fr.uge.revue.dto.response.SendResponseDTO;
import fr.uge.revue.dto.review.*;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Response;
import fr.uge.revue.model.User;
import fr.uge.revue.service.CommentService;
import fr.uge.revue.service.ResponseService;
import fr.uge.revue.service.ReviewService;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ReviewRestController {
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final ResponseService responseService;
    private final UserService userService;

    public ReviewRestController(ReviewService reviewService, CommentService commentService,
                                ResponseService responseService, UserService userService) {
        this.reviewService = Objects.requireNonNull(reviewService);
        this.commentService = Objects.requireNonNull(commentService);
        this.responseService = Objects.requireNonNull(responseService);
        this.userService = Objects.requireNonNull(userService);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewOneReviewDTO> oneReview(@PathVariable long reviewId, Authentication authentication) {
        var review = reviewService.getReview(reviewId);
        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (authentication != null && authentication.isAuthenticated()) {
            var userId = ((User) authentication.getPrincipal()).getId();
            var user = userService.findUserWithLikesAndDislikes(userId).orElseThrow();
            return ResponseEntity.ok().body(ReviewOneReviewDTO.from(review.get(), user));
        }
        return ResponseEntity.ok().body(ReviewOneReviewDTO.from(review.get()));
    }

    @GetMapping("/reviews/{reviewId}/unitTests")
    public ResponseEntity<TestsReviewResponseDTO> getUnitTests(@PathVariable long reviewId) {
        var review = reviewService.getReview(reviewId);
        return review.map(value -> ResponseEntity.ok().body(new TestsReviewResponseDTO(value.getUnitTests())))
                .orElseGet(() -> ResponseEntity.notFound().build());
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

    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<CommentDTO> createComment(@PathVariable long reviewId, @RequestBody String content, Authentication authentication) {
        Objects.requireNonNull(content);
        var user = (User) authentication.getPrincipal();
        var review = reviewService.getReview(reviewId);
        if(review.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        var comment = new Comment(content, user, review.get());
        commentService.saveComment(comment);
        return ResponseEntity.ok(CommentDTO.from(comment));
    }

    @PostMapping("/reviews/{reviewId}/response")
    public ResponseEntity<ResponseDTO> createResponse(@RequestBody SendResponseDTO content, Authentication authentication) {
        Objects.requireNonNull(content);
        var user = (User) authentication.getPrincipal();
        var comment = commentService.getCommentWithResponse(content.id());
        if(comment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        var response = new Response(content.content(), user,comment.get());
        responseService.saveResponse(response);
        return ResponseEntity.ok(ResponseDTO.from(response));
    }

    @PostMapping("/responses/{responseId}/like")
    public ResponseEntity<Integer> toggleResponseLikeButton(@PathVariable long responseId, Authentication authentication) {
        var response = responseService.getResponse(responseId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        System.out.println(responseId);
        System.out.println(userId);
        var like = userService.toggleLikeResponse(userId, response);
        if(like == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(like);
        }
        return ResponseEntity.ok(like);
    }

    @PostMapping("/responses/{responseId}/dislike")
    public ResponseEntity<Integer> toggleResponseDisLikeButton(@PathVariable long responseId, Authentication authentication) {
        var response = responseService.getResponse(responseId).orElseThrow();
        var userId = ((User) authentication.getPrincipal()).getId();
        var like = userService.toggleDislikeResponse(userId, response);
        if(like == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(like);
    }
}
