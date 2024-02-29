package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.resetpassword.AskEmailDTO;
import fr.uge.revue.dto.resetpassword.InvalidPasswordDTO;
import fr.uge.revue.dto.resetpassword.NotFoundOrExpiredToken;
import fr.uge.revue.dto.resetpassword.ResetPasswordDTO;
import fr.uge.revue.service.EmailService;
import fr.uge.revue.service.ResetPasswordService;
import fr.uge.revue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ResetPasswordRestController {
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    private final EmailService emailService;

    public ResetPasswordRestController(UserService userService, ResetPasswordService resetPasswordService, EmailService emailService) {
        this.userService = Objects.requireNonNull(userService);
        this.resetPasswordService = Objects.requireNonNull(resetPasswordService);
        this.emailService = Objects.requireNonNull(emailService);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> createResetPasswordToken(HttpServletRequest request, @Valid @RequestBody AskEmailDTO formContent,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        var userFound = userService.findByEmail(formContent.email());
        if (userFound.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var user = userFound.get();
        var token = UUID.randomUUID().toString();
        resetPasswordService.createResetPasswordTokenForUser(user, token);

        var link = getLink(request, token);
        emailService.send(user.getEmail(), "Reset Password", buildEmailContent(link));

        return ResponseEntity.ok().build();
    }

    private static String getLink(HttpServletRequest request, String token) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/front/resetPassword/" + token;
    }

    private static String buildEmailContent(String link) {
        return "Cliquer sur le lien suivant pour changer votre mot de passe : " + link;
    }

    @GetMapping("/resetPassword/{token}/validate")
    public ResponseEntity<?> validateToken(@PathVariable("token") String token) {
        var resetTokenFound = resetPasswordService.findByToken(token);
        if (resetTokenFound.isEmpty() || resetTokenFound.get().isExpired()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new InvalidPasswordDTO("Le mot de passe est invalide"));
        }

        var resetTokenFound = resetPasswordService.findByToken(token);
        if (resetTokenFound.isEmpty() || resetTokenFound.get().isExpired()) {
            return ResponseEntity.notFound().build();
        }
        resetPasswordService.updateUserPassword(resetTokenFound.get(), resetPasswordDTO.password());
        return ResponseEntity.ok().build();
    }
}
