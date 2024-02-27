package fr.uge.revue.dto.user;

import javax.validation.constraints.Size;

public record UserLoginDTO(String username, @Size(min = 4) String password) {
}
