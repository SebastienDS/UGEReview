package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.user.UserSignUpDTO;
import fr.uge.revue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

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

}
