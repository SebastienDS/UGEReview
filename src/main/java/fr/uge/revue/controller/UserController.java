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
import java.util.regex.Pattern;

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

    @PutMapping("/users/{userId}/updateUsername")
    public ResponseEntity<String> updateUsername(@PathVariable long userId, @RequestBody String newUsername, Authentication authentication){
        Objects.requireNonNull(newUsername);
        Objects.requireNonNull(authentication);
        var user = (User) authentication.getPrincipal();
        var userProfile = userService.getUserById(userId);
        if(userProfile.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!user.getUsername().equals(userProfile.get().getUsername())){
            //WTF le hacker
            System.out.println(user.getUsername() + " " + userProfile.get().getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Users doesn't match");
        }
        if(userService.getUsernames().contains(newUsername) || newUsername.equals("")){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Username already taken");
        }
        userService.setUsername(userId, newUsername);
        var newData = userService.getUserById(userId).orElseThrow();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(newData, newData.getPassword(), newData.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return ResponseEntity.ok("Ok");
    }

    @PutMapping("/users/{userId}/updateEmail")
    public ResponseEntity<String> updateEmail(@PathVariable long userId, @RequestBody String newEmail, Authentication authentication){
        Objects.requireNonNull(newEmail);
        Objects.requireNonNull(authentication);
        var user = (User) authentication.getPrincipal();
        var userProfile = userService.getUserById(userId);
        if(userProfile.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!user.getUsername().equals(userProfile.get().getUsername())){
            //WTF le hacker
            System.out.println(user.getUsername() + " " + userProfile.get().getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Users doesn't match");
        }
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(newEmail);
        if(!matcher.matches()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Not Valid");
        }
        if(userService.getEmails().contains(newEmail)){
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Username already taken");
        }
        userService.setEmail(userId, newEmail);
        var newData = userService.getUserById(userId).orElseThrow();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(newData, newData.getPassword(), newData.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return ResponseEntity.ok("Ok");
    }

    private record PasswordReceived(String oldPassword, String newPassword){}

    @PutMapping("/users/{userId}/updatePassword")
    public ResponseEntity<String> updatePassword(@PathVariable long userId, @RequestBody PasswordReceived passwords, Authentication authentication){
        Objects.requireNonNull(passwords);
        Objects.requireNonNull(authentication);
        var user = (User) authentication.getPrincipal();
        var userProfile = userService.getUserById(userId);
        var oldPassword = passwords.oldPassword; //TODO: encode
        var newPassword = passwords.newPassword; //TODO: encode / maybe add constraint
        if(userProfile.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(!user.getUsername().equals(userProfile.get().getUsername())){
            //WTF le hacker
            System.out.println(user.getUsername() + " " + userProfile.get().getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Users doesn't match");
        }
        if(!user.getPassword().equals(passwords.oldPassword())){
            System.out.println("lack encode " + oldPassword + " " + newPassword);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Password");
        }
        userService.updateUserPassword(user, newPassword);
        var newData = userService.getUserById(userId).orElseThrow();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(newData, newData.getPassword(), newData.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return ResponseEntity.ok("Ok");
    }
}


