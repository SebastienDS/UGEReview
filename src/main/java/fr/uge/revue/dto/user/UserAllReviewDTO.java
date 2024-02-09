package fr.uge.revue.dto.user;

import fr.uge.revue.model.User;

public record UserAllReviewDTO(String username) {
    public static UserAllReviewDTO from(User author) {
        return new UserAllReviewDTO(author.getUsername());
    }
}
