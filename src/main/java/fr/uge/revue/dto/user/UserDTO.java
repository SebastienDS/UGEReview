package fr.uge.revue.dto.user;

import fr.uge.revue.model.User;

public record UserDTO(long id, String username, String email) {
    public static UserDTO from(User author) {
        return new UserDTO(author.getId(), author.getUsername(), author.getEmail());
    }
}
