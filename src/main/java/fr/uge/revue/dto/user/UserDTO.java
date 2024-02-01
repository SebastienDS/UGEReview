package fr.uge.revue.dto.user;

import fr.uge.revue.model.Role;
import fr.uge.revue.model.User;

public record UserDTO(String username, String email, Role role) {
    public static UserDTO from(User author) {
        return new UserDTO(author.getUsername(), author.getEmail(), author.getRole());
    }
}
