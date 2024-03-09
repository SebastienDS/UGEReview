package fr.uge.revue.controller.rest;

import fr.uge.revue.dto.user.UserLoginDTO;
import fr.uge.revue.dto.user.UserDataDTO;
import fr.uge.revue.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public ResponseEntity<UserDataDTO> login(@RequestBody UserLoginDTO user) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.username(), user.password())
            );
            var userRole = (User) authentication.getPrincipal();
            return ResponseEntity.ok().body(new UserDataDTO(userRole.getId(), userRole.getRole()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
