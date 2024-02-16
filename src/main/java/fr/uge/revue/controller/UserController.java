package fr.uge.revue.controller;

import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.model.User;
import fr.uge.revue.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

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
}


