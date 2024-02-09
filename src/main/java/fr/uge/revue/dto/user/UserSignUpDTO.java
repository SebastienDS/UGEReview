package fr.uge.revue.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public record UserSignUpDTO(
        String username,
        @Email(regexp = "^.+@.+$")
        String email,
        @Size(min = 4)
        String password) {
}
