package fr.uge.revue.controller.mvc;

import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Objects;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(Model model, @Valid @ModelAttribute UserSignUpDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registered", false);
            // TODO: show errors in template
            return "signup";
        }
        var registered = userService.register(user);
        model.addAttribute("registered", registered);
        return "signup";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
