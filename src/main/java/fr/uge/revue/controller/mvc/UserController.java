package fr.uge.revue.controller.mvc;

import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.updatePassword.PasswordReceived;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Objects;
import java.util.regex.Pattern;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable long userId, Model model, Authentication authentication) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return "notFound";
        }

        if(user.get().getRole() == Role.ADMIN){
            model.addAttribute("isUserPageAdmin", true);
        }
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authenticated", true);
            var myId = ((User) authentication.getPrincipal()).getId();
            var me = userService.findByIdWithFollowers(myId).orElseThrow();
            if(me.getRole() == Role.ADMIN){
                model.addAttribute("isUserAdmin", true);
            }
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

    @PostMapping("/banProfile")
    public RedirectView banProfile(Authentication authentication, @RequestParam("id") long id) {
        if(authentication == null || (!authentication.isAuthenticated())){
            return new RedirectView("/users/" + id);
        }
        var user = (User) authentication.getPrincipal();
        if(user.getRole() != Role.ADMIN){
            return new RedirectView("/users/" + id);
        }
        var userPage = userService.getUserById(id);
        if(userPage.isEmpty() || userPage.get().getRole() == Role.ADMIN){
            return new RedirectView("/users/" + id);
        }
        userService.banUser(id);
        return new RedirectView("/reviews");
    }

    @GetMapping("/users/{userId}/comments")
    public String getUserComments(@PathVariable long userId, Model model) {
        var comments = userService.getComments(userId);
        model.addAttribute("comments", comments);
        return "comments";
    }

    @GetMapping("/users/{userId}/responses")
    public String getUserResponses(@PathVariable long userId, Model model) {
        var responses = userService.getResponses(userId);
        model.addAttribute("responses", responses);
        return "responses";
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

    @PutMapping("/users/{userId}/updatePassword")
    public ResponseEntity<String> updatePassword(@PathVariable long userId, @Valid @RequestBody PasswordReceived passwords,
                                                 Authentication authentication, BCryptPasswordEncoder passwordEncoder,
                                                 BindingResult bindingResult){
        Objects.requireNonNull(passwords);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password not valid");
        }
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
        if(!passwordEncoder.matches(passwords.oldPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Password");
        }
        userService.updateUserPassword(user, passwords.newPassword());
        var newData = userService.getUserById(userId).orElseThrow();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(newData, newData.getPassword(), newData.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/users/{userId}/likes")
    public String getLikedContents(@PathVariable long userId, Model model) {
        model.addAttribute("likedList",  userService.getLikedListFromUser(userId));
        return "likes";
    }

    @GetMapping("/profile")
    public RedirectView profile(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return new RedirectView("/users/" + user.getId());
    }

    @GetMapping("/users/{userId}/follows")
    public String getUserFollows(@PathVariable long userId, Authentication authentication, Model model) {
        var user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return "notFound";
        }
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authenticated", true);
            var myId = ((User) authentication.getPrincipal()).getId();
            if (userId == myId) {
                model.addAttribute("isMyUserPage", true);
            }
        }
        model.addAttribute("followsList",  userService.getFollows(user.get().getId()).stream().map(UserDTO::from).toList());
        return "follows";
    }
}

