package fr.uge.revue.dto.review;

import fr.uge.revue.model.Review;

import java.util.Date;

public record ReviewAllReviewDTO(String title, Date date, int like) {
    public static ReviewAllReviewDTO from(Review review) {
        return new ReviewAllReviewDTO(review.getTitle(),review.getDate(), review.getLikes());
    }
}
