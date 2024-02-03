package fr.uge.revue.controller;

import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/signup")
    public String signup(Model model, @Valid @ModelAttribute UserSignUpDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registered", false);
            return "signup";
        }
        var registered = registrationService.register(user);
        model.addAttribute("registered", registered);
        return "signup";
    }
}
