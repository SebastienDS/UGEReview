package fr.uge.revue.dto.user;

import fr.uge.revue.model.User;

import java.util.Date;

public record UserProfileDTO(String username, String email, Date dateCreation) {
    public static UserProfileDTO from(User user){
        return new UserProfileDTO(user.getUsername(), user.getEmail(), user.getCreationDate());
    }
}
