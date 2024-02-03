package fr.uge.revue.service;

import fr.uge.revue.dto.user.UserSignUpDTO;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserService userService;

    public RegistrationService(UserService userService) {
        this.userService = userService;
    }

    public boolean register(UserSignUpDTO user) {
        return userService.register(user);
    }
}
