package fr.uge.revue.dto.updatePassword;

import javax.validation.constraints.Size;

public record PasswordReceived(
    @Size(min = 4) String oldPassword,
    @Size(min = 4) String newPassword
){}