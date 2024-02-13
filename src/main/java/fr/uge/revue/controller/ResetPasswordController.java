package fr.uge.revue.controller;

import fr.uge.revue.dto.resetpassword.AskEmailDTO;
import fr.uge.revue.dto.resetpassword.ResetPasswordDTO;
import fr.uge.revue.service.EmailService;
import fr.uge.revue.service.ResetPasswordService;
import fr.uge.revue.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

@Controller
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;
    private final UserService userService;
    private final EmailService emailService;

    public ResetPasswordController(ResetPasswordService resetPasswordService, UserService userService, EmailService emailService) {
        this.resetPasswordService = Objects.requireNonNull(resetPasswordService);
        this.userService = Objects.requireNonNull(userService);
        this.emailService = Objects.requireNonNull(emailService);
    }

    @GetMapping("/resetPassword")
    public String askResetPasswordPage() {
        return "resetPassword/askResetPassword";
    }

    @PostMapping("/resetPassword")
    public String createResetPasswordToken(Model model, HttpServletRequest request,
                                           @Valid @ModelAttribute AskEmailDTO formContent, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            return "resetPassword/askResetPassword";
        }

        var userFound = userService.findByEmail(formContent.email());
        if (userFound.isEmpty()) {
            model.addAttribute("success", false);
            return "resetPassword/askResetPassword";
        }
        var user = userFound.get();
        var token = UUID.randomUUID().toString();
        resetPasswordService.createResetPasswordTokenForUser(user, token);

        var link = request.getRequestURL().toString() + "/" + token;
        emailService.send(user.getEmail(), "Reset Password", buildEmailContent(link));

        model.addAttribute("success", true);
        return "resetPassword/askResetPassword";
    }

    private static String buildEmailContent(String link) {
        return "Cliquer sur le lien suivant pour changer votre mot de passe : " + link;
    }

    @GetMapping("/resetPassword/{token}")
    public String showResetPasswordPage(Model model, @PathVariable("token") String token) {
        var resetTokenFound = resetPasswordService.findByToken(token);
        if (resetTokenFound.isEmpty() || resetTokenFound.get().isExpired()) {
            return "resetPassword/invalidToken";
        }
        return "resetPassword/resetPasswordForm";
    }

    @PostMapping("/resetPassword/{token}")
    public String resetPassword(Model model, @PathVariable("token") String token,
                                @Valid @ModelAttribute ResetPasswordDTO resetPasswordDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordError", "Le mot de passe est invalide");
            return "resetPassword/resetPasswordForm";
        }

        var resetTokenFound = resetPasswordService.findByToken(token);
        if (resetTokenFound.isEmpty() || resetTokenFound.get().isExpired()) {
            return "resetPassword/invalidToken";
        }
        resetPasswordService.updateUserPassword(resetTokenFound.get(), resetPasswordDTO.password());
        return "resetPassword/success";
    }
}
