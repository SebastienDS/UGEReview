package fr.uge.revue.dto.review;

import fr.uge.revue.dto.comment.CommentDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Review;

import java.util.Comparator;
import java.util.List;

public record ReviewOneReviewDTO(long id, String title, String code, String test, int likes, UserDTO author, List<CommentDTO> comments) {
    public static ReviewOneReviewDTO from(Review review) {
        var comments = review.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDate))
                .map(CommentDTO::from)
                .toList();
        return new ReviewOneReviewDTO(review.getId(), review.getTitle(), review.getCode(), review.getTest(), review.getLikes(),
                UserDTO.from(review.getAuthor()), comments);
    }
}