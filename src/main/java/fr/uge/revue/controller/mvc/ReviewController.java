package fr.uge.revue.controller.mvc;

import fr.uge.revue.dto.notification.NotificationDTO;
import fr.uge.revue.dto.response.SendResponseDTO;
import fr.uge.revue.dto.review.CreateReviewDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.review.ReviewOneReviewDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.service.CommentService;
import fr.uge.revue.service.ResponseService;
import fr.uge.revue.service.NotificationService;
import fr.uge.revue.service.ReviewService;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Objects;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final CommentService commentService;
    private final ResponseService responseService;
    private final NotificationService notificationService;

    public ReviewController(ReviewService reviewService, UserService userService, CommentService commentService, ResponseService responseService, NotificationService notificationService) {
        this.reviewService = Objects.requireNonNull(reviewService);
        this.userService = Objects.requireNonNull(userService);
        this.commentService = Objects.requireNonNull(commentService);
        this.responseService = Objects.requireNonNull(responseService);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    @GetMapping("/")
    public RedirectView goToReviews() {
        return new RedirectView("/reviews");
    }

    @GetMapping("/reviews")
    public String allReviews(Model model, Authentication authentication, @RequestParam(defaultValue = "") String currentSearch,
                             @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        if (authentication != null && authentication.isAuthenticated() && currentSearch.equals("")) {
            var user = (User) authentication.getPrincipal();
            model.addAttribute("authenticated", true);
            model.addAttribute("notifications", getNotifications(user));
            var reviews = reviewService.getFriendsReview(user, pageNumber, pageSize).stream().map(ReviewAllReviewDTO::from).toList();
            model.addAttribute("reviews", reviews);
            return "reviews";
        }
        if(!currentSearch.equals("")){
            model.addAttribute("currentSearch", currentSearch);
            var reviews = reviewService.searchReviewPage(currentSearch, pageNumber, pageSize).stream().map(ReviewAllReviewDTO::from).toList();
            model.addAttribute("reviews", reviews);
            return "reviews";
        }
        var reviews = reviewService.getReviews(pageNumber, pageSize).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    private List<NotificationDTO> getNotifications(User user) {
        return notificationService.findAllUserNotifications(user.getId())
                .stream()
                .map(NotificationDTO::from)
                .toList();
    }

    @PostMapping("/reviews")
    public String searchReview(@ModelAttribute("search") String search, Model model, Authentication authentication,
                               @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        if (authentication != null && authentication.isAuthenticated()) {
            var user = (User) authentication.getPrincipal();
            model.addAttribute("authenticated", true);
            model.addAttribute("notifications", getNotifications(user));
        }
        model.addAttribute("currentSearch", search);
        var reviews = reviewService.searchReviewPage(search, pageNumber, pageSize).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @GetMapping("/reviews/{reviewId}")
    public String oneReview(@PathVariable long reviewId, Model model, Authentication authentication) {
        var review = reviewService.getReview(reviewId);
        if (review.isEmpty()) {
            return "notFound";
        }

        if (authentication != null && authentication.isAuthenticated()) {
            var userId = ((User) authentication.getPrincipal()).getId();
            var user = userService.findUserWithLikesAndDislikes(userId).orElseThrow();

            model.addAttribute("authenticated", true);
            model.addAttribute("notifications", getNotifications(user));
            model.addAttribute("isUserAdmin", user.getRole() == Role.ADMIN);
            model.addAttribute("notificationActivated", notificationService.isUserRequestingNotification(reviewId, user.getId()));
            model.addAttribute("review", ReviewOneReviewDTO.from(review.get(), user));
        } else {
            model.addAttribute("review", ReviewOneReviewDTO.from(review.get()));
        }

        model.addAttribute("reviewId", reviewId);
        return "review";
    }

    @PostMapping("/reviews/{reviewId}/like")
    public RedirectView toggleReviewLikeButton(@PathVariable long reviewId, Authentication authentication) {
        var review = reviewService.getReview(reviewId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleLikeReview(user, review);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/reviews/{reviewId}/dislike")
    public RedirectView toggleReviewDisLikeButton(@PathVariable long reviewId, Authentication authentication) {
        var review = reviewService.getReviewWithLikes(reviewId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleDislikeReview(user, review);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/comments/{commentId}/like")
    public RedirectView toggleCommentLikeButton(@PathVariable long commentId, Authentication authentication) {
        var comment = commentService.getCommentWithLikes(commentId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleLikeComment(user, comment);
        return new RedirectView("/reviews/" + comment.getReview().getId() + "#comment_" + commentId);
    }

    @PostMapping("/comments/{commentId}/dislike")
    public RedirectView toggleCommentDisLikeButton(@PathVariable long commentId, Authentication authentication) {
        var comment = commentService.getCommentWithLikes(commentId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleDislikeComment(user, comment);
        return new RedirectView("/reviews/" + comment.getReview().getId() + "#comment_" + commentId);
    }

    @PostMapping("/responses/{responseId}/like")
    public RedirectView toggleResponseLikeButton(@PathVariable long responseId, Authentication authentication) {
        var response = responseService.getResponseWithLike(responseId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleLikeResponse(user, response);
        return new RedirectView("/reviews/" + response.getComment().getReview().getId() + "#response_" + responseId);
    }

    @PostMapping("/responses/{responseId}/dislike")
    public RedirectView toggleResponseDisLikeButton(@PathVariable long responseId, Authentication authentication) {
        var response = responseService.getResponseWithLike(responseId).orElseThrow();
        var user = ((User) authentication.getPrincipal());
        userService.toggleDislikeResponse(user, response);
        return new RedirectView("/reviews/" + response.getComment().getReview().getId() + "#response_" + responseId);
    }

    @GetMapping("/createReview")
    public String getCreateReviewForm() {
        return "/createReview";
    }

    @PostMapping("/createReview")
    public RedirectView createReview(Authentication authentication, @ModelAttribute CreateReviewDTO createReviewDTO) {
        var user = (User) authentication.getPrincipal();
        var review = reviewService.createReview(createReviewDTO, user);
        return new RedirectView("/reviews/" + review.getId());
    }

    @PostMapping("/reviews/{reviewId}/comment")
    public ResponseEntity<String> createComment(@PathVariable long reviewId, @RequestBody String content, Authentication authentication) {
        Objects.requireNonNull(content);
        var user = (User) authentication.getPrincipal();
        var review = reviewService.getReview(reviewId);
        if(review.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review Not Found");
        }
        var comment = new Comment(content, user, review.get());
        commentService.saveComment(comment);
        return ResponseEntity.ok("");
    }

    @PostMapping("/reviews/{reviewId}/response")
    public ResponseEntity<String> createResponse(@RequestBody SendResponseDTO content, Authentication authentication) {
        Objects.requireNonNull(content);
        var user = (User) authentication.getPrincipal();
        var comment = commentService.getCommentWithResponse(content.id());
        if(comment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment Not Found");
        }
        var response = new Response(content.content(), user,comment.get());
        responseService.saveResponse(response);
        return ResponseEntity.ok("");
    }

    @PostMapping("/deleteReview")
    public RedirectView deleteReview(Authentication authentication, @RequestParam("id") long id) {
        var user = (User) authentication.getPrincipal();
        if(user.getRole() != Role.ADMIN){
            return new RedirectView("/reviews/" + id);
        }
        var success = reviewService.delete(id);
        if(!success){
            return new RedirectView("/reviews/" + id);
        }
        return new RedirectView("/reviews");
    }

    @PostMapping("/deleteComment")
    public RedirectView deleteComment(Authentication authentication, @RequestParam("id") long id, @RequestParam("reviewId") long reviewId) {
        var user = (User) authentication.getPrincipal();
        if(user.getRole() != Role.ADMIN){
            return new RedirectView("/reviews/" + reviewId);
        }

        commentService.delete(id);
        return new RedirectView("/reviews/" + reviewId);
    }

    @PostMapping("/deleteResponse")
    public RedirectView deleteResponse(Authentication authentication, @RequestParam("id") long id, @RequestParam("reviewId") long reviewId) {
        var user = (User) authentication.getPrincipal();
        if(user.getRole() != Role.ADMIN){
            return new RedirectView("/reviews/" + reviewId);
        }

        responseService.delete(id);
        return new RedirectView("/reviews/" + reviewId);
    }
}
