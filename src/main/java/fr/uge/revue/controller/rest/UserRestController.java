package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.comment.CommentUserDTO;
import fr.uge.revue.dto.likeable.LikeableDTO;
import fr.uge.revue.dto.response.ResponseUserDTO;
import fr.uge.revue.dto.review.ReviewAllReviewDTO;
import fr.uge.revue.dto.user.update.EmailReceivedDTO;
import fr.uge.revue.dto.user.update.PasswordReceivedDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.dto.user.UserFollowStateDTO;
import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.dto.user.update.UsernameReceivedDTO;
import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;
import fr.uge.revue.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
    public ResponseEntity<List<ReviewAllReviewDTO>> showUserReviews(@PathVariable long userId,
                                                                    @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        var reviews = userService.findAllUserReviews(userId, pageNumber, pageSize).stream().map(ReviewAllReviewDTO::from).toList();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentUserDTO>> getUserComments(@PathVariable long userId,
                                                                @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        pageNumber = Math.max(pageNumber, 0);
        pageSize = Math.max(pageSize, 1);
        var comments = userService.getComments(userId, pageNumber, pageSize).stream().map(CommentUserDTO::from).toList();
        return ResponseEntity.ok().body(comments);
    }

    @GetMapping("/users/{userId}/responses")
    public ResponseEntity<List<ResponseUserDTO>> getUserResponses(@PathVariable long userId,
                                                                  @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        var responses = userService.getResponses(userId, pageNumber, pageSize).stream().map(ResponseUserDTO::from).toList();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<List<LikeableDTO>> getLikedContents(@PathVariable long userId,
                                                              @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok().body(userService.getLikedListFromUser(userId, pageNumber, pageSize));
    }

    @PutMapping("/users/{userId}/updateUsername")
    public ResponseEntity<String> updateUsername(@PathVariable long userId, @RequestBody UsernameReceivedDTO usernameReceivedDTO, Authentication authentication){
        Objects.requireNonNull(usernameReceivedDTO);
        var newUsername = usernameReceivedDTO.newUsername();
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
    public ResponseEntity<String> updateEmail(@PathVariable long userId, @RequestBody EmailReceivedDTO emailReceivedDTO, Authentication authentication){
        Objects.requireNonNull(emailReceivedDTO);
        var newEmail = emailReceivedDTO.newEmail();
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
    public ResponseEntity<String> updatePassword(@PathVariable long userId, @Valid @RequestBody PasswordReceivedDTO passwords,
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

    @PostMapping("/banProfile")
    public ResponseEntity<String> banProfile(Authentication authentication, @RequestBody long id) {
        if(authentication == null || (!authentication.isAuthenticated())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var user = (User) authentication.getPrincipal();
        if(user.getRole() != Role.ADMIN){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userPage = userService.getUserById(id);
        if(userPage.isEmpty() || userPage.get().getRole() == Role.ADMIN){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.banUser(id);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/users/{userId}/follows")
    public ResponseEntity<List<UserDTO>> getUserFollows(@PathVariable long userId,
                                                        @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseEntity.ok().body(userService.getFollows(userId, pageNumber, pageSize).stream().map(UserDTO::from).toList());
    }

    @GetMapping("/searchComments")
    public ResponseEntity<List<String>> searchComments(@RequestParam String search, Authentication authentication) {
        var userId = ((User) authentication.getPrincipal()).getId();
        var comments = userService.searchComments(userId, search);
        return ResponseEntity.ok().body(comments);
    }

}
