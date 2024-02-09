package fr.uge.revue.dto.review;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Review;

import java.util.List;

public record ReviewOneReviewDTO(String title, String code, String test, int likes, UserDTO author, List<Comment> comments) {
    public static ReviewOneReviewDTO from(Review review) {
        return new ReviewOneReviewDTO(review.getTitle(), review.getCode(), review.getTest(), review.getLikes(), UserDTO.from(review.getAuthor()), review.getComments());
        }
}