package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.user.UserLoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class RegistrationRestController {

    private final AuthenticationManager authenticationManager;

    public RegistrationRestController(AuthenticationManager authenticationManager) {
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.username(), user.password())
            );
            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record TestResponse(String value) {}

    @GetMapping("/test")
    public ResponseEntity<TestResponse> test(Authentication authentication) {
        System.out.println(authentication);
        return ResponseEntity.ok().body(new TestResponse("test"));
    }
}
