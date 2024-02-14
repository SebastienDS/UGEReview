package fr.uge.revue.dto.review;

public record CreateReviewDTO(
        String title,
        String commentary,
        String code,
        String test) {
}
