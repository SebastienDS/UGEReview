package fr.uge.revue.dto.resetpassword;

import javax.validation.constraints.Size;

public record ResetPasswordDTO(
        @Size(min = 4) String password
) {
}
