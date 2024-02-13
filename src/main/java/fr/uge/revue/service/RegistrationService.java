package fr.uge.revue.service;

import fr.uge.revue.dto.user.UserSignUpDTO;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RegistrationService {
    private final UserService userService;

    public RegistrationService(UserService userService) {
        Objects.requireNonNull(userService);
        this.userService = userService;
    }

    public boolean register(UserSignUpDTO user) {
        Objects.requireNonNull(user);
        return userService.register(user);
    }
}
