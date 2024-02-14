package fr.uge.revue.controller;

import fr.uge.revue.dto.user.UserProfileDTO;
import fr.uge.revue.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        Objects.requireNonNull(userService);
        this.userService = userService;
    }

    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable("userId")long userId, Model model, Authentication authentication) {
        var user = userService.getUserById(userId);
        if(user.isEmpty()){
            return "notFound";
        }
        model.addAttribute("user", UserProfileDTO.from(user.get()));
        return "user";
    }
}
