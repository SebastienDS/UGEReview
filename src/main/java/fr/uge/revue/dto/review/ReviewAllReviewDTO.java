package fr.uge.revue.dto.review;

import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Review;

import java.util.Date;

public record ReviewAllReviewDTO(long id, UserDTO author, String title, Date date, int likes) {
    public static ReviewAllReviewDTO from(Review review) {
        return new ReviewAllReviewDTO(review.getId(), UserDTO.from(review.getAuthor()), review.getTitle(), review.getDate(), review.getLikes());
    }
}
