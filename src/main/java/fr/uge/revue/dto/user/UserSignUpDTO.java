package fr.uge.revue.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

public record UserSignUpDTO(
        String username,
        @Email
        String email,
        @Min(4)
        String password) {
}
