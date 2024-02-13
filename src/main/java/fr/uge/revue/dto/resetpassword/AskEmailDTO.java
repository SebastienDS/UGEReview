package fr.uge.revue.dto.resetpassword;

import javax.validation.constraints.Email;

public record AskEmailDTO(
        @Email(regexp = "^.+@.+$")
        String email
) {
}
