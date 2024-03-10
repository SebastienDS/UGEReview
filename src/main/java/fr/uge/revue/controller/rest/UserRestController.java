package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.comment.CommentDTO;
import fr.uge.revue.dto.comment.CommentUserDTO;
import fr.uge.revue.dto.likeable.LikeableDTO;
import fr.uge.revue.dto.response.ResponseDTO;
import fr.uge.revue.dto.response.ResponseUserDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.user.UserFollowStateDTO;
import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.model.*;
import fr.uge.revue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpDTO> signup(@Valid @RequestBody UserSignUpDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // TODO: show errors in template
            return ResponseEntity.badRequest().build();
        }
        userService.register(user);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserProfileDTO> userProfile(@PathVariable long userId) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(UserProfileDTO.from(user.get()));
    }

    @GetMapping("/users/{userId}/follow/state")
    public ResponseEntity<UserFollowStateDTO> isFollow(@PathVariable long userId, Authentication authentication) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();

        }
        var myId = ((User) authentication.getPrincipal()).getId();
        var me = userService.findByIdWithFollowers(myId).orElseThrow();
        return ResponseEntity.ok().body(new UserFollowStateDTO(me.getFollowers().contains(user.get())));
    }

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable long userId, Authentication authentication) {
        var myId = ((User) authentication.getPrincipal()).getId();
        userService.follow(myId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable long userId, Authentication authentication) {
        var myId = ((User) authentication.getPrincipal()).getId();
        userService.unfollow(myId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteProfile")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        userService.deleteUser(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewAllReviewDTO>> showUserReviews(@PathVariable long userId, Model model) {
        var reviews = userService.findAllUserReviews(userId).stream().sorted(Comparator.comparing(Review::getDate).reversed()).map(ReviewAllReviewDTO::from).toList();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentUserDTO>> getUserComments(@PathVariable long userId, Model model) {
        var comments = userService.getComments(userId).stream().sorted(Comparator.comparing(Comment::getDate).reversed()).map(CommentUserDTO::from).toList();
        return ResponseEntity.ok().body(comments);
    }

    @GetMapping("/users/{userId}/responses")
    public ResponseEntity<List<ResponseUserDTO>> getUserResponses(@PathVariable long userId, Model model) {
        var responses = userService.getResponses(userId).stream().sorted(Comparator.comparing(Response::getDate).reversed()).map(ResponseUserDTO::from).toList();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<List<LikeableDTO>> getLikedContents(@PathVariable long userId, Model model) {
        var user = userService.getUserWithLikes(userId);
        var likedList = Stream.of(user.reviews(), user.comments(), user.responses())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Likeable::getDate).reversed())
                .map(LikeableDTO::from)
                .toList();
        return ResponseEntity.ok().body(likedList);
    }
}
