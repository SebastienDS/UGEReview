package fr.uge.revue.dto.review;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Review;

public record ReviewDTO(String title, String code, String test, UserDTO author) {
    public static ReviewDTO from(Review review) {
        return new ReviewDTO(review.getTitle(), review.getCode(), review.getTest(), UserDTO.from(review.getAuthor()));
    }
}