package fr.uge.revue.controller;

import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.model.User;
import fr.uge.revue.service.ReviewService;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Controller
public class UserController {
    private final UserService userService;
    private final ReviewService reviewService;

    public UserController(UserService userService, ReviewService reviewService) {
        this.reviewService = Objects.requireNonNull(reviewService);
        this.userService = Objects.requireNonNull(userService);
    }

    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable long userId, Model model, Authentication authentication) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return "notFound";
        }
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authenticated", true);
            var myId = ((User) authentication.getPrincipal()).getId();
            var me = userService.findByIdWithFollowers(myId).orElseThrow();

            if (me.equals(user.get())) {
                model.addAttribute("isMyUserPage", true);
            } else if (me.getFollowers().contains(user.get())) {
                model.addAttribute("follow", false);
                model.addAttribute("followUrl", "/users/" + userId + "/unfollow");
            } else {
                model.addAttribute("follow", true);
                model.addAttribute("followUrl", "/users/" + userId + "/follow");
            }
        }

        model.addAttribute("user", UserProfileDTO.from(user.get()));
        model.addAttribute("userId", userId);
        return "user";
    }

    @PostMapping("/users/{userId}/follow")
    public RedirectView followUser(@PathVariable long userId, Authentication authentication) {
        var myId = ((User) authentication.getPrincipal()).getId();
        userService.follow(myId, userId);
        return new RedirectView("/users/" + userId);
    }

    @PostMapping("/users/{userId}/unfollow")
    public RedirectView unfollowUser(@PathVariable long userId, Authentication authentication) {
        var myId = ((User) authentication.getPrincipal()).getId();
        userService.unfollow(myId, userId);
        return new RedirectView("/users/" + userId);
    }

    @PostMapping("/deleteProfile")
    public RedirectView deleteUser(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        userService.deleteUser(user.getId());
        return new RedirectView("/logout");
    }

    @GetMapping("/users/{userId}/comments")
    public String getUserComments(@PathVariable long userId, Model model) {
        var comments = userService.getComments(userId);
        model.addAttribute("comments", comments);
        return "comments";
    }

    @GetMapping("/users/{userId}/reviews")
    public String showUserReviews(@PathVariable long userId, Model model) {
        var reviews = userService.findAllUserReviews(userId).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PostMapping("/users/{userId}/reviews")
    public String showUserReviewsWithFilter(@PathVariable long userId, @ModelAttribute("search") String search, Model model) {
        var reviews = userService.findAllUserReviewsMatching(userId, search).stream().map(ReviewAllReviewDTO::from).toList();
        model.addAttribute("reviews", reviews);
        return "reviews";
    }

    @PutMapping("/users/{userId}/update")
    public ResponseEntity<String> modifyData(@PathVariable long userId, @RequestBody User newUser, Authentication authentication){
        Objects.requireNonNull(newUser);
        Objects.requireNonNull(authentication);
        var user = (User) authentication.getPrincipal();
        var userProfile = userService.getUserById(userId);
        if(userProfile.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!user.getUsername().equals(userProfile.get().getUsername())){
            //WTF le hacker
            System.out.println(user.getUsername() + " " + userProfile.get().getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not User");
        }
        if(userService.getUsernames().contains(newUser.getUsername()) || newUser.getUsername().equals("")){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Username already taken");
        }
        userService.setUsername(userId, newUser.getUsername());
        var newData = userService.getUserById(userId).orElseThrow();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(newData, newData.getPassword(), newData.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return ResponseEntity.ok("Ok");
    }
}


