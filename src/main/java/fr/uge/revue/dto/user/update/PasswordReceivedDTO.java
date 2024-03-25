package fr.uge.revue.dto.user.update;

import javax.validation.constraints.Size;

public record PasswordReceivedDTO(
    @Size(min = 4) String oldPassword,
    @Size(min = 4) String newPassword
){}