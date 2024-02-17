package fr.uge.revue.dto.review;

import fr.uge.revue.model.Review;

import java.util.Date;

public record ReviewAllReviewDTO(long id, String title, Date date, int likes) {
    public static ReviewAllReviewDTO from(Review review) {
        return new ReviewAllReviewDTO(review.getId(), review.getTitle(), review.getDate(), review.getLikes());
    }
}
