package fr.uge.revue.dto.review;

import fr.uge.revue.dto.comment.CommentDTO;
import fr.uge.revue.dto.user.UserDTO;
import fr.uge.revue.model.Comment;
import fr.uge.revue.model.Review;
import fr.uge.revue.model.TestsReview;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public record ReviewOneReviewDTO(long id, String title, String commentary, String code, String test, int likes, UserDTO author, Date date, List<CommentDTO> comments, TestsReview tests) {
    public static ReviewOneReviewDTO from(Review review) {
        var comments = review.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDate))
                .map(CommentDTO::from)
                .toList();
        return new ReviewOneReviewDTO(review.getId(), review.getTitle(), review.getCommentary(), review.getCode(), review.getTest(), review.getLikes(),
                UserDTO.from(review.getAuthor()), review.getDate(), comments, review.getTests());
    }
}